package org.betonquest.betonquest.integration;

import com.google.common.base.Suppliers;
import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.IntegrationData;
import org.betonquest.betonquest.lib.integration.PluginProvider;
import org.betonquest.betonquest.lib.integration.policy.PluginPolicy;
import org.betonquest.betonquest.lib.integration.policy.VanillaPolicy;
import org.betonquest.betonquest.lib.integration.policy.VersionedPluginPolicy;
import org.betonquest.betonquest.lib.integration.policy.VersionedPolicy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     * Gets all enabled integrations.
     *
     * @return all enabled integrations with their metadata
     */
    public List<? extends IntegrationData> getEnabledIntegrations() {
        return enabledIntegrations;
    }

    /**
     * Registers a new integration with the manager.
     * <p>
     * This method registers an integration that will be enabled when its policy requirements are met.
     * Policies define the conditions under which the integration should be enabled, such as the presence
     * of a specific plugin, minecraft version, or other custom requirements.
     * <p>
     * Integrations must be registered before the server finishes enabling plugins. Attempting to
     * register an integration after {@link #postEnable(BetonQuestApiService)} has been called will
     * result in an {@link IllegalStateException}.
     * <p>
     * If an integration is registered while the manager is already in the {@code ENABLED} state,
     * it will be enabled immediately if its requirements are met.
     *
     * @param integration         the supplier for the integration to register
     * @param integrationProvider the plugin providing the integration
     * @param policies            the policies defining requirements for the integration
     * @throws IllegalStateException if called after {@link #postEnable(BetonQuestApiService)}
     */
    public void register(final Supplier<Integration> integration, final Plugin integrationProvider, final Set<Policy> policies) {
        final IntegrationWrapper integrationWrapper = new IntegrationWrapper(loggerFactory.create(IntegrationWrapper.class),
                Suppliers.memoize(integration::get), integrationProvider, policies, new AtomicBoolean(false));
        if (currentState == ManagerState.POST_ENABLED) {
            throw new IllegalStateException("Integrations can only be registered before the server is done enabling plugins. Attempted: Plugin '%s' for '%s'"
                    .formatted(integrationProvider.getName(), integrationWrapper.integratedPluginVersionName()));
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
                log.warn("Could not enable an integration provided by plugin '%s' for '%s'."
                        .formatted(wrapper.integrationProvider.getName(), wrapper.integratedPluginVersionName()));
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
     * @param logger              the logger to use
     * @param integration         the integration supplier to encapsulate
     * @param integrationProvider the plugin providing the integration
     * @param policies            the policies defining the requirements for the integration to be enabled
     * @param enabled             the atomic boolean to track whether the enabled state is set to prevent
     *                            repeatedly enabling the integration
     */
    private record IntegrationWrapper(BetonQuestLogger logger, Supplier<Integration> integration,
                                      Plugin integrationProvider, Set<Policy> policies, AtomicBoolean enabled)
            implements IntegrationData {

        /**
         * The 'unspecified' string to use when no plugin name is available.
         */
        private static final String UNSPECIFIED = "unspecified";

        /**
         * Checks if the integration is ready to be enabled.
         * <p>
         * An integration is ready to be enabled if both the integrated plugin and the integrator plugin are enabled.
         *
         * @return true if the integration is ready to be enabled, false otherwise
         */
        private boolean isReadyToEnabled() {
            return integrationProvider.isEnabled() && policies.stream()
                    .filter(PluginPolicy.class::isInstance)
                    .map(PluginPolicy.class::cast)
                    .map(PluginPolicy::pluginProvider)
                    .map(PluginProvider::plugin)
                    .allMatch(plugin -> plugin.map(Plugin::isEnabled).orElse(false));
        }

        /**
         * Gets the name the integration is made for along with its version.
         * If the integration is made for the server itself without specifying a plugin,
         * the current server implementation is named instead.
         *
         * @return the name and version the integration is made for
         */
        private String integratedPluginVersionName() {
            return policies.stream().filter(VersionedPolicy.class::isInstance)
                    .map(VersionedPolicy.class::cast)
                    .map(policy -> "%s (%s)".formatted(policy.name(), policy.version()))
                    .findFirst().orElse(UNSPECIFIED);
        }

        /**
         * Checks if the integration's required minimal version is compatible with the current version.
         *
         * @return true if the integration is compatible, false otherwise
         */
        private boolean isCompatible() {
            for (final Policy policy : policies) {
                if (!policy.validate()) {
                    logger.warn("Integration provided by '%s' is not compatible due to policy: %s"
                            .formatted(integrationProvider.getName(), policy.description()));
                    return false;
                }
            }
            return true;
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
                        .formatted(integrationProvider.getName(), integratedPluginVersionName(), phase));
                return true;
            } catch (final QuestException e) {
                logger.warn("Could not %s integration provided by plugin '%s' for '%s': %s"
                        .formatted(phase, integrationProvider.getName(), integratedPluginVersionName(), e.getMessage()), e);
            } catch (final Throwable e) {
                logger.error("Could not %s integration provided by plugin '%s' for '%s' because of an unexpected error: %s"
                        .formatted(phase, integrationProvider.getName(), integratedPluginVersionName(), e.getMessage()), e);
            }
            return false;
        }

        /**
         * Enables the integration.
         *
         * @param service the service to obtain the plugin's api
         */
        private boolean enable(final BetonQuestApiService service) {
            return !enabled.getAndSet(true) && callSafely("enable", () -> integration.get().enable(service.api(integrationProvider)));
        }

        /**
         * Post-enables the integration.
         *
         * @param service the service to obtain the plugin's api
         */
        private void postEnable(final BetonQuestApiService service) {
            callSafely("post-enable", () -> integration.get().postEnable(service.api(integrationProvider)));
        }

        /**
         * Tears down the integration.
         */
        private void disable() {
            callSafely("disable", () -> integration.get().disable());
        }

        @Override
        public Integration getIntegration() {
            return integration.get();
        }

        @Override
        public List<Triple<String, String, String>> getDisplayInfo() {
            final List<Triple<String, String, String>> list = new ArrayList<>();
            for (final Policy policy : policies) {
                if (policy instanceof final VersionedPluginPolicy pluginPolicy) {
                    list.add(Triple.of(
                            pluginPolicy.pluginProvider().name().orElse(UNSPECIFIED),
                            pluginPolicy.versionCompareStrategy().getRepresentation() + pluginPolicy.version(),
                            pluginPolicy.description()
                    ));
                } else if (policy instanceof final PluginPolicy pluginPolicy) {
                    list.add(Triple.of(
                            pluginPolicy.pluginProvider().name().orElse(UNSPECIFIED),
                            pluginPolicy.pluginProvider().version().map(Object::toString).orElse(UNSPECIFIED),
                            pluginPolicy.description()
                    ));
                } else if (policy instanceof final VanillaPolicy vanillaPolicy) {
                    list.add(Triple.of(
                            vanillaPolicy.name(),
                            vanillaPolicy.versionCompareStrategy().getRepresentation() + vanillaPolicy.version(),
                            vanillaPolicy.description()
                    ));
                }
            }
            return list;
        }
    }
}
