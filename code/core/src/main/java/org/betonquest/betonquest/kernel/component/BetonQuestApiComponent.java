package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.bukkit.BukkitManager;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.DefaultBetonQuestApiService;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.compass.CompassManager;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Set;
import java.util.function.Function;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultBetonQuestApi}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class BetonQuestApiComponent extends AbstractCoreComponent {

    /**
     * Create a new BetonQuestApiComponent.
     */
    public BetonQuestApiComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, ServicesManager.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class,
                Identifiers.class, Instructions.class, Actions.class, Conditions.class, Objectives.class,
                Placeholders.class, Conversations.class, Items.class, Npcs.class, FontRegistry.class, Reloader.class,
                Persistence.class, CompassManager.class, Translations.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultBetonQuestApiService.class, DefaultBetonQuestApi.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final ServicesManager servicesManager = getDependency(ServicesManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Actions actions = getDependency(Actions.class);
        final Conditions conditions = getDependency(Conditions.class);
        final Objectives objectives = getDependency(Objectives.class);
        final Placeholders placeholders = getDependency(Placeholders.class);
        final Conversations conversations = getDependency(Conversations.class);
        final Items items = getDependency(Items.class);
        final Npcs npcs = getDependency(Npcs.class);
        final FontRegistry fontRegistry = getDependency(FontRegistry.class);
        final Reloader reloader = getDependency(Reloader.class);
        final Persistence persistence = getDependency(Persistence.class);
        final CompassManager compassManager = getDependency(CompassManager.class);
        final Translations translations = getDependency(Translations.class);
        final Plugin plugin = getDependency(Plugin.class);

        final BetonQuestLogger serviceLogger = loggerFactory.create(BetonQuestApiService.class);
        final Function<Plugin, BetonQuestApi> defaultBetonQuestApiGenerator = callerPlugin -> {
            serviceLogger.debug("Loading API for plugin %s version %s".formatted(callerPlugin.getName(), callerPlugin.getDescription().getVersion()));
            final BukkitManager bukkitManager = new DefaultBukkitManager(callerPlugin);
            return new DefaultBetonQuestApi(callerPlugin, profileProvider, packManager, loggerFactory, instructions,
                    actions, conditions, objectives, placeholders, items, npcs, conversations, identifiers,
                    fontRegistry, reloader, persistence, compassManager, translations, bukkitManager);
        };
        final DefaultBetonQuestApiService defaultBetonQuestApiService = new DefaultBetonQuestApiService(defaultBetonQuestApiGenerator);
        servicesManager.register(BetonQuestApiService.class, defaultBetonQuestApiService, plugin, ServicePriority.Highest);

        dependencyProvider.take(DefaultBetonQuestApiService.class, defaultBetonQuestApiService);
        dependencyProvider.take(DefaultBetonQuestApi.class, (DefaultBetonQuestApi) defaultBetonQuestApiGenerator.apply(plugin));
    }

    /**
     * The default implementation of the {@link BukkitManager}.
     *
     * @param plugin the plugin this manager and the api is created for
     */
    /* default */ record DefaultBukkitManager(Plugin plugin) implements BukkitManager {

        @Override
        public void registerEvents(final Listener listener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * The default implementation of the {@link BetonQuestApi}.
     *
     * @param attachedPlugin the plugin this api instance is created for
     * @param profiles       the profile provider handling profiles for players
     * @param packages       the package manager for quest packages
     * @param loggerFactory  the logger factory to create loggers for individual services
     * @param instructions   the instruction api accessor
     * @param actions        the actions api accessor
     * @param conditions     the conditions api accessor
     * @param objectives     the objectives api accessor
     * @param placeholders   the placeholders api accessor
     * @param items          the item api accessor
     * @param npcs           the npc api accessor
     * @param conversations  the conversation api accessor
     * @param identifiers    the identifier api accessor
     * @param fonts          the font registry
     * @param reloader       the reloader
     * @param persistence    the persistence api accessor
     * @param compasses      the compass manager
     * @param translations   the translations
     * @param bukkit         the bukkit manager
     */
    /* default */ record DefaultBetonQuestApi(Plugin attachedPlugin, ProfileProvider profiles,
                                              QuestPackageManager packages,
                                              BetonQuestLoggerFactory loggerFactory, Instructions instructions,
                                              Actions actions, Conditions conditions, Objectives objectives,
                                              Placeholders placeholders, Items items, Npcs npcs,
                                              Conversations conversations, Identifiers identifiers,
                                              FontRegistry fonts, Reloader reloader,
                                              Persistence persistence, CompassManager compasses,
                                              Translations translations,
                                              BukkitManager bukkit) implements BetonQuestApi {

    }
}
