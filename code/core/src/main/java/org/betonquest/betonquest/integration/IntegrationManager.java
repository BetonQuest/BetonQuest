package org.betonquest.betonquest.integration;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.betonquest.betonquest.lib.versioning.MinecraftVersion;
import org.betonquest.betonquest.lib.versioning.UpdateStrategy;
import org.betonquest.betonquest.lib.versioning.Version;
import org.betonquest.betonquest.lib.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * The {@link IntegrationManager} handles all registered integrations.
 */
public class IntegrationManager {

    /**
     * The logger that will be used for logging.
     */
    private final BetonQuestLogger log;

    /**
     * The logger factory to create loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The list of registered integrations.
     */
    private final List<IntegrationWrapper> integrations;

    /**
     * The list of successfully enabled integrations.
     */
    private final List<IntegrationWrapper> enabledIntegrations;

    /**
     * The current state of the integration manager.
     */
    private ManagerState currentState;

    /**
     * Creates a new instance of the integration manager.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory
     */
    public IntegrationManager(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        this.log = log;
        this.loggerFactory = loggerFactory;
        this.integrations = new ArrayList<>();
        this.enabledIntegrations = new ArrayList<>();
        this.currentState = ManagerState.PRE_ENABLE;
    }

    /**
     * Registers a new integration with the manager.
     * <p>
     * This method registers an integration that will be enabled when the corresponding plugin
     * or minecraft version becomes available. For vanilla (minecraft-version based) integrations,
     * explicitly use {@link PluginProvider#EMPTY} as the {@code integratedPluginProvider}.
     * <p>
     * Integrations must be registered before the server finishes enabling plugins. Attempting to
     * register an integration after {@link #postEnable(BetonQuestApiService)} has been called will
     * result in an {@link IllegalStateException}.
     * <p>
     * If an integration is registered while the manager is already in the {@code ENABLED} state,
     * it will be enabled immediately if its requirements are met.
     * <p>
     * The {@code minimalVersion} parameter specifies the minimal compatible version requirement.
     * For plugin-based integrations, it represents the minimal version of the integrated plugin.
     * For vanilla integrations, it represents the minimal minecraft version required.
     * Pass {@code null} if there is no version requirement.
     *
     * @param integration              the supplier for the integration to register
     * @param integratedPluginProvider the plugin provider of the plugin the integration is made for,
     *                                 or {@link PluginProvider#EMPTY} for minecraft-version based integrations
     * @param integratorPlugin         the plugin that is registering this integration
     * @param minimalVersion           the minimal compatible version requirement, or {@code null} if none
     * @throws IllegalStateException if called after {@link #postEnable(BetonQuestApiService)}
     */
    public void register(final Supplier<Integration> integration, final PluginProvider integratedPluginProvider,
                         final Plugin integratorPlugin, @Nullable final Version minimalVersion) {
        final IntegrationWrapper integrationWrapper = new IntegrationWrapper(loggerFactory.create(IntegrationWrapper.class),
                Suppliers.memoize(integration::get), integratedPluginProvider, integratorPlugin, minimalVersion, new AtomicBoolean(false));
        if (currentState == ManagerState.POST_ENABLED) {
            throw new IllegalStateException("Integrations can only be registered before the server is done enabling plugins. Attempted: Plugin '%s' for '%s'"
                    .formatted(integratorPlugin.getName(), integrationWrapper.integratedPluginVersionName()));
        }
        integrations.add(integrationWrapper);
        if (currentState == ManagerState.ENABLED) {
            enable(Objects.requireNonNull(Bukkit.getServicesManager().load(BetonQuestApiService.class)));
        }
    }

    /**
     * Attempts to enable all integrations that are ready to be enabled.
     * <p>
     * When {@code pluginToEnable} is {@link PluginProvider#EMPTY}, this enables integrations that are
     * minecraft-version based (vanilla integrations) rather than plugin-based integrations.
     *
     * @param service the service to obtain the plugin's api
     */
    public void enable(final BetonQuestApiService service) {
        if (currentState == ManagerState.POST_ENABLED) {
            log.warn("Attempted to enable integrations after the server has already finished enabling plugins. Ignoring.");
            return;
        }
        final List<IntegrationWrapper> wrappers = integrations.stream()
                .filter(wrapper -> !enabledIntegrations.contains(wrapper))
                .filter(IntegrationWrapper::isReadyToEnabled)
                .toList();
        log.debug("Enabling %s valid integrations out of %s registered integrations. %s are already enabled."
                .formatted(wrappers.size(), integrations.size(), enabledIntegrations.size()));
        for (final IntegrationWrapper wrapper : wrappers) {
            if (!wrapper.isCompatible()) {
                log.warn("Could not enable an integration provided by plugin '%s' for '%s': Version requirement not met."
                        .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginVersionName()));
                continue;
            }
            if (wrapper.enable(service)) {
                enabledIntegrations.add(wrapper);
            }
        }
        if (currentState == ManagerState.PRE_ENABLE) {
            currentState = ManagerState.ENABLED;
        }
    }

    /**
     * Post-enables all enabled integrations.
     *
     * @param service the service to obtain the plugin's api
     */
    public void postEnable(final BetonQuestApiService service) {
        currentState = ManagerState.POST_ENABLED;
        enabledIntegrations.forEach(wrapper -> wrapper.postEnable(service));
    }

    /**
     * Tears down all enabled integrations.
     */
    public void disable() {
        enabledIntegrations.forEach(IntegrationWrapper::disable);
    }

    /**
     * The state of the integration manager.
     */
    private enum ManagerState {
        /**
         * After initialization and before any integrations are enabled.
         */
        PRE_ENABLE,
        /**
         * After at least one integration has been enabled.
         */
        ENABLED,
        /**
         * After the integrations have enabled and are about to be post-enabled.
         */
        POST_ENABLED
    }

    /**
     * Encapsulates an integration along with its associated plugin provider and minimal compatible version.
     * This provides methods to determine the compatibility of the integration and to manage its lifecycle.
     * The lifecycle consists of enabling, post-enabling, and tearing down the integration.
     *
     * @param logger                   the logger to use
     * @param integration              the integration supplier to encapsulate
     * @param integratedPluginProvider the plugin provider of the plugin the integration is made for
     *                                 or {@link PluginProvider#EMPTY}
     * @param integratorPlugin         the plugin that registered the integration
     * @param minimalVersion           the minimal compatible version to check against
     *                                 or null if no version check is required
     * @param enabled                  the atomic boolean to track whether the enabled state is set to prevent
     *                                 repeatedly enabling the integration
     */
    private record IntegrationWrapper(BetonQuestLogger logger, Supplier<Integration> integration,
                                      PluginProvider integratedPluginProvider,
                                      Plugin integratorPlugin,
                                      @Nullable Version minimalVersion,
                                      AtomicBoolean enabled) {

        /**
         * Checks if the integration is ready to be enabled.
         * <p>
         * An integration is ready to be enabled if both the integrated plugin and the integrator plugin are enabled.
         *
         * @return true if the integration is ready to be enabled, false otherwise
         */
        private boolean isReadyToEnabled() {
            return (integratedPluginProvider == PluginProvider.EMPTY || integratedPluginProvider.plugin().map(Plugin::isEnabled).orElse(false))
                    && integratorPlugin.isEnabled();
        }

        /**
         * Gets the name the integration is made for along with its version.
         * If the integration is made for the server itself without specifying a plugin,
         * the current server implementation is named instead.
         *
         * @return the name and version the integration is made for
         */
        private String integratedPluginVersionName() {
            if (integratedPluginProvider == PluginProvider.EMPTY) {
                return Bukkit.getName() + (minimalVersion == null ? "" : " (" + minimalVersion + ")");
            }
            return integratedPluginProvider.name().orElse("unspecified") + (integratedPluginProvider.version().isEmpty() ? "" : " (" + integratedPluginProvider.version().get() + ")");
        }

        /**
         * Checks if the integration is a minecraft-version based integration.
         * <p>
         * Essentially, this checks if the integrated plugin provider is {@link PluginProvider#EMPTY}.
         * While an empty plugin provider is considered an illegal state, this explicit instance is used to
         * differentiate between vanilla and plugin-based integrations.
         *
         * @return true if the integration is a vanilla integration, false otherwise
         */
        private boolean isVanillaIntegration() {
            return integratedPluginProvider == PluginProvider.EMPTY;
        }

        /**
         * Checks if the integration's required minimal version is compatible with the current version.
         *
         * @return true if the integration is compatible, false otherwise
         */
        private boolean isCompatible() {
            if (minimalVersion == null) {
                return true;
            }
            final Version actualVersion = isVanillaIntegration() ? new MinecraftVersion() : integratedPluginProvider.version().orElseThrow();
            final boolean compatible = actualVersion.isCompatibleWith(new VersionComparator(UpdateStrategy.MAJOR), minimalVersion);
            if (!compatible) {
                logger.debug("Integration provided by plugin '%s' is not compatible with the current version. [%s < %s]"
                        .formatted(integratorPlugin.getName(), actualVersion, minimalVersion));
            }
            return compatible;
        }

        /**
         * Calls the given runnable safely by catching any exceptions.
         *
         * @param runnable the runnable to call
         */
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        private boolean callSafely(final String phase, final QuestRunnable runnable) {
            try {
                runnable.run();
                logger.debug("Integration provided by plugin '%s' for '%s' %sd."
                        .formatted(integratorPlugin.getName(), integratedPluginVersionName(), phase));
                return true;
            } catch (final QuestException e) {
                logger.warn("Could not %s integration provided by plugin '%s' for '%s': %s"
                        .formatted(phase, integratorPlugin.getName(), integratedPluginVersionName(), e.getMessage()), e);
            } catch (final Throwable e) {
                logger.error("Could not %s integration provided by plugin '%s' for '%s' because of an unexpected error: %s"
                        .formatted(phase, integratorPlugin.getName(), integratedPluginVersionName(), e.getMessage()), e);
            }
            return false;
        }

        /**
         * Enables the integration.
         *
         * @param service the service to obtain the plugin's api
         */
        private boolean enable(final BetonQuestApiService service) {
            return !enabled.getAndSet(true) && callSafely("enable", () -> integration.get().enable(service.api(integratorPlugin)));
        }

        /**
         * Post-enables the integration.
         *
         * @param service the service to obtain the plugin's api
         */
        private void postEnable(final BetonQuestApiService service) {
            callSafely("post-enable", () -> integration.get().postEnable(service.api(integratorPlugin)));
        }

        /**
         * Tears down the integration.
         */
        private void disable() {
            callSafely("disable", () -> integration.get().disable());
        }
    }
}
