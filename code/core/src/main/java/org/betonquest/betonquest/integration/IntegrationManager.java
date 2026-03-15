package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.versioning.MinecraftVersion;
import org.betonquest.betonquest.lib.versioning.UpdateStrategy;
import org.betonquest.betonquest.lib.versioning.Version;
import org.betonquest.betonquest.lib.versioning.VersionComparator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    private final List<IntegrationWrapper> enabledIntegrations = new ArrayList<>();

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
    }

    /**
     * Registers a new integration with the manager.
     *
     * @param integration              the integration to register
     * @param integratedPluginProvider the plugin provider of the plugin the integration is made for
     * @param integratorPlugin         the plugin that registered the integration
     * @param minimalVersion           the minimal compatible version of the plugin the integration is made for,
     *                                 the minimal compatible version of minecraft the integration is made for,
     *                                 or null if the integration is not restricted by version compatibility
     */
    public void register(final Integration integration, final PluginProvider integratedPluginProvider, final Plugin integratorPlugin, @Nullable final Version minimalVersion) {
        final IntegrationWrapper integrationWrapper = new IntegrationWrapper(loggerFactory.create(IntegrationWrapper.class), integration, integratedPluginProvider, integratorPlugin, minimalVersion);
        integrations.add(integrationWrapper);
    }

    /**
     * Enables the integration for the given plugin.
     *
     * @param service        the service to obtain the plugin's api
     * @param pluginToEnable the plugin to enable the integration for
     */
    public void enable(final BetonQuestApiService service, final PluginProvider pluginToEnable) {
        final List<IntegrationWrapper> wrappers = integrations.stream()
                .filter(integration -> integration.integratedPluginProvider().plugin().equals(pluginToEnable.plugin()))
                .toList();
        log.debug("Enabling %s valid integrations out of %s registered integrations".formatted(wrappers.size(), integrations.size()));
        for (final IntegrationWrapper wrapper : wrappers) {
            try {
                if (wrapper.isCompatible()) {
                    wrapper.enable(service);
                    enabledIntegrations.add(wrapper);
                    log.debug("Integration provided by '%s' for plugin '%s' enabled."
                            .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID")));
                } else {
                    log.warn("Could not load an integration provided by '%s' for plugin '%s': Version requirement not met."
                            .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID")));
                }
            } catch (final QuestException e) {
                log.warn("Could not load an integration provided by '%s' for plugin '%s': %s"
                        .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID"), e.getMessage()), e);
            }
        }
    }

    /**
     * Post-enables all enabled integrations.
     *
     * @param service the service to obtain the plugin's api
     */
    public void postEnable(final BetonQuestApiService service) {
        for (final IntegrationWrapper wrapper : enabledIntegrations) {
            try {
                wrapper.postEnable(service);
                log.debug("Integration provided by '%s' for plugin '%s' post-enabled."
                        .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID")));
            } catch (final QuestException e) {
                log.warn("Could not post-enable integration provided by '%s' for plugin '%s': %s"
                        .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID"), e.getMessage()), e);
            }
        }
    }

    /**
     * Tears down all enabled integrations.
     */
    public void teardown() {
        for (final IntegrationWrapper wrapper : enabledIntegrations) {
            try {
                wrapper.teardown();
                log.debug("Integration provided by '%s' for plugin '%s' disabled."
                        .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID")));
            } catch (final QuestException e) {
                log.warn("Could not disable integration provided by '%s' for plugin '%s': %s"
                        .formatted(wrapper.integratorPlugin.getName(), wrapper.integratedPluginProvider.name().orElse("INVALID"), e.getMessage()), e);
            }
        }
    }

    /**
     * Encapsulates an integration along with its associated plugin provider and minimal compatible version.
     * This provides methods to determine the compatibility of the integration and to manage its lifecycle.
     * The lifecycle consists of enabling, post-enabling, and tearing down the integration.
     */
    record IntegrationWrapper(BetonQuestLogger logger, Integration integration, PluginProvider integratedPluginProvider,
                              Plugin integratorPlugin,
                              @Nullable Version minimalVersion) {

        /**
         * Checks if the integration is a minecraft-version based integration.
         *
         * @return true if the integration is a vanilla integration, false otherwise
         */
        boolean isVanillaIntegration() {
            return integratedPluginProvider == PluginProvider.EMPTY;
        }

        /**
         * Checks if the integrations required minimal version is compatible with the current version.
         *
         * @return true if the integration is compatible, false otherwise
         */
        boolean isCompatible() {
            if (minimalVersion == null) {
                return true;
            }
            final Version versionToCompare = isVanillaIntegration() ? new MinecraftVersion() : integratedPluginProvider.version().orElseThrow();
            final boolean compatible = versionToCompare.isCompatibleWith(new VersionComparator(UpdateStrategy.PATCH), minimalVersion);
            if (!compatible) {
                logger.debug("Integration provided by plugin '%s' is not compatible with the current version. [%s < %s]".formatted(integratorPlugin.getName(), versionToCompare, minimalVersion));
            }
            return compatible;
        }

        /**
         * Enables the integration.
         *
         * @param service the service to obtain the plugin's api
         * @throws QuestException if the integration could not be enabled
         */
        void enable(final BetonQuestApiService service) throws QuestException {
            integration.enable(service.api(integratorPlugin));
        }

        /**
         * Post-enables the integration.
         *
         * @param service the service to obtain the plugin's api
         * @throws QuestException if the integration could not be post-enabled
         */
        void postEnable(final BetonQuestApiService service) throws QuestException {
            integration.postEnable(service.api(integratorPlugin));
        }

        /**
         * Tears down the integration.
         *
         * @throws QuestException if the integration could not be disabled
         */
        void teardown() throws QuestException {
            integration.disable();
        }
    }
}
