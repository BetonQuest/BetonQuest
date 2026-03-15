package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.integration.IntegrationManager;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link IntegrationManager}.
 */
public class IntegrationComponent extends AbstractCoreComponent {

    /**
     * Create a new IntegrationComponent.
     */
    public IntegrationComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginManager.class, BetonQuestApiService.class, IntegrationManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final PluginManager pluginManager = getDependency(PluginManager.class);
        final IntegrationManager integrationManager = getDependency(IntegrationManager.class);
        final BetonQuestApiService betonQuestApiService = getDependency(BetonQuestApiService.class);

        pluginManager.registerEvents(new IntegrationEnableListener(integrationManager, betonQuestApiService), plugin);
        integrationManager.enable(betonQuestApiService);
    }

    /**
     * The listener that loads integrations when their plugin is enabled.
     *
     * @param integrationManager   the integration manager
     * @param betonQuestApiService the beton quest api service
     */
    public record IntegrationEnableListener(IntegrationManager integrationManager,
                                            BetonQuestApiService betonQuestApiService) implements Listener {

        /**
         * The event handler that catches plugin enable events.
         *
         * @param event the event
         */
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(final PluginEnableEvent event) {
            integrationManager.enable(betonQuestApiService);
        }
    }
}
