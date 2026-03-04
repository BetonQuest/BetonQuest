package org.betonquest.betonquest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.migrator.Migrator;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.component.ActionsComponent;
import org.betonquest.betonquest.kernel.component.ArgumentParsersComponent;
import org.betonquest.betonquest.kernel.component.AsyncSaverComponent;
import org.betonquest.betonquest.kernel.component.BStatsMetricsComponent;
import org.betonquest.betonquest.kernel.component.BetonQuestApiComponent;
import org.betonquest.betonquest.kernel.component.CancelersComponent;
import org.betonquest.betonquest.kernel.component.CommandsComponent;
import org.betonquest.betonquest.kernel.component.CompassComponent;
import org.betonquest.betonquest.kernel.component.CompatibilityComponent;
import org.betonquest.betonquest.kernel.component.ConditionsComponent;
import org.betonquest.betonquest.kernel.component.ConfigAccessorFactoryComponent;
import org.betonquest.betonquest.kernel.component.ConfigComponent;
import org.betonquest.betonquest.kernel.component.ConversationColorsComponent;
import org.betonquest.betonquest.kernel.component.ConversationsComponent;
import org.betonquest.betonquest.kernel.component.DataLoaderComponent;
import org.betonquest.betonquest.kernel.component.DatabaseComponent;
import org.betonquest.betonquest.kernel.component.ExecutionCacheComponent;
import org.betonquest.betonquest.kernel.component.FontRegistryComponent;
import org.betonquest.betonquest.kernel.component.GlobalDataComponent;
import org.betonquest.betonquest.kernel.component.IdentifiersComponent;
import org.betonquest.betonquest.kernel.component.InstructionsComponent;
import org.betonquest.betonquest.kernel.component.ItemsComponent;
import org.betonquest.betonquest.kernel.component.JournalsComponent;
import org.betonquest.betonquest.kernel.component.LanguageProviderComponent;
import org.betonquest.betonquest.kernel.component.ListenersComponent;
import org.betonquest.betonquest.kernel.component.LogHandlerComponent;
import org.betonquest.betonquest.kernel.component.MigratorComponent;
import org.betonquest.betonquest.kernel.component.NotificationCategoriesComponent;
import org.betonquest.betonquest.kernel.component.NotificationsComponent;
import org.betonquest.betonquest.kernel.component.NpcsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
import org.betonquest.betonquest.kernel.component.PersistenceComponent;
import org.betonquest.betonquest.kernel.component.PlaceholdersComponent;
import org.betonquest.betonquest.kernel.component.PlayerDataStorageComponent;
import org.betonquest.betonquest.kernel.component.PlayerHiderComponent;
import org.betonquest.betonquest.kernel.component.PluginMessageComponent;
import org.betonquest.betonquest.kernel.component.PostEnableComponent;
import org.betonquest.betonquest.kernel.component.ProfileProviderComponent;
import org.betonquest.betonquest.kernel.component.QuestPackageManagerComponent;
import org.betonquest.betonquest.kernel.component.RPGMenuComponent;
import org.betonquest.betonquest.kernel.component.ReloaderComponent;
import org.betonquest.betonquest.kernel.component.SchedulesComponent;
import org.betonquest.betonquest.kernel.component.TextParserComponent;
import org.betonquest.betonquest.kernel.component.TextSectionParserComponent;
import org.betonquest.betonquest.kernel.component.UpdaterComponent;
import org.betonquest.betonquest.kernel.component.VersionInfoComponent;
import org.betonquest.betonquest.kernel.component.types.ActionTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ConditionTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ConversationIOTypesComponent;
import org.betonquest.betonquest.kernel.component.types.InterceptorTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ItemTypesComponent;
import org.betonquest.betonquest.kernel.component.types.NotifyIOTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ObjectiveTypeComponent;
import org.betonquest.betonquest.kernel.component.types.PlaceholderTypeComponent;
import org.betonquest.betonquest.kernel.component.types.ScheduleTypesComponent;
import org.betonquest.betonquest.kernel.component.types.TextParserTypesComponent;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.lib.dependency.component.DefaultCoreComponentLoader;
import org.betonquest.betonquest.lib.dependency.component.RequirementComponentWrapper;
import org.betonquest.betonquest.lib.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.betonquest.betonquest.schedule.ActionScheduling;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Map;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings("NullAway.Init")
public class BetonQuest extends JavaPlugin {

    /**
     * The BetonQuest Plugin instance.
     */
    private static BetonQuest instance;

    /**
     * The custom logger for the plugin.
     */
    private BetonQuestLogger log;

    /**
     * The core component loader instance.
     */
    private CoreComponentLoader coreComponentLoader;

    /**
     * The required default constructor without arguments for plugin creation.
     */
    public BetonQuest() {
        super();
    }

    /**
     * Get the plugin's instance.
     *
     * @return The plugin's instance.
     */
    public static BetonQuest getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        final BetonQuestLoggerFactory loggerFactory = new CachingBetonQuestLoggerFactory(new DefaultBetonQuestLoggerFactory());
        this.log = loggerFactory.create(this);

        final DefaultCoreComponentLoader coreComponentLoader = new DefaultCoreComponentLoader(loggerFactory.create(DefaultCoreComponentLoader.class));
        coreComponentLoader.init(BetonQuestLoggerFactory.class, loggerFactory);
        this.coreComponentLoader = coreComponentLoader;
        initPluginDependencies(coreComponentLoader);
        registerComponents(coreComponentLoader);
        registerFeatures(coreComponentLoader);
        registerTypesComponents(coreComponentLoader);
        this.coreComponentLoader.load();

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            log.warn("Could not disable /betonquestanswer logging", e);
        }

        log.info("BetonQuest successfully enabled!");
    }

    private void initPluginDependencies(final CoreComponentLoader coreComponentLoader) {
        coreComponentLoader.init(JavaPlugin.class, this);
        coreComponentLoader.init(Server.class, getServer());
        coreComponentLoader.init(PluginManager.class, getServer().getPluginManager());
        coreComponentLoader.init(BukkitScheduler.class, getServer().getScheduler());
        coreComponentLoader.init(PluginDescriptionFile.class, getDescription());
        coreComponentLoader.init(ServicesManager.class, getServer().getServicesManager());
    }

    private void registerTypesComponents(final CoreComponentLoader coreComponentLoader) {
        List.of(
                new ActionTypesComponent(),
                new ConditionTypesComponent(),
                new ObjectiveTypeComponent(),
                new PlaceholderTypeComponent(),
                new ConversationIOTypesComponent(),
                new InterceptorTypesComponent(),
                new ItemTypesComponent(),
                new NotifyIOTypesComponent(),
                new ScheduleTypesComponent(),
                new TextParserTypesComponent()
        ).forEach(coreComponentLoader::register);
    }

    private void registerFeatures(final CoreComponentLoader coreComponentLoader) {
        List.of(
                new ArgumentParsersComponent(),
                new InstructionsComponent(),
                new BetonQuestApiComponent(),
                new BStatsMetricsComponent(),
                new CompatibilityComponent(),
                new IdentifiersComponent(),
                new ConditionsComponent(),
                new ActionsComponent(),
                new ObjectivesComponent(),
                new PlaceholdersComponent(),
                new TextParserComponent(),
                new PlayerDataStorageComponent(),
                new TextSectionParserComponent(),
                new SchedulesComponent(),
                new NotificationsComponent(),
                new JournalsComponent(),
                new PluginMessageComponent(),
                new ItemsComponent(),
                new CompassComponent(),
                new ConversationsComponent(),
                new NpcsComponent(),
                new CancelersComponent(),
                new RPGMenuComponent()
        ).forEach(coreComponentLoader::register);
    }

    private void registerComponents(final CoreComponentLoader coreComponentLoader) {
        List.of(
                new VersionInfoComponent(),
                new ProfileProviderComponent(),
                new ConfigAccessorFactoryComponent(),
                new MigratorComponent(),
                new ReloaderComponent(),
                new RequirementComponentWrapper(new ConfigComponent(), Migrator.class),
                new LanguageProviderComponent(),
                new CommandsComponent(),
                new LogHandlerComponent(),
                new QuestPackageManagerComponent(),
                new NotificationCategoriesComponent(),
                new DatabaseComponent(),
                new AsyncSaverComponent(),
                new GlobalDataComponent(),
                new FontRegistryComponent(),
                new ListenersComponent(),
                new PlayerHiderComponent(),
                new UpdaterComponent(this.getFile()),
                new PersistenceComponent(),
                new ConversationColorsComponent(),
                new ExecutionCacheComponent(),
                new DataLoaderComponent(),
                new PostEnableComponent()
        ).forEach(coreComponentLoader::register);
    }

    @Override
    public void onDisable() {
        coreComponentLoader.getOptional(ActionScheduling.class).ifPresent(ActionScheduling::clear);

        coreComponentLoader.getOptional(ConversationProcessor.class).map(ConversationProcessor::getActiveConversations).map(Map::values)
                .ifPresent(conversations -> conversations.forEach(Conversation::suspend));
        coreComponentLoader.getOptional(ProfileProvider.class).map(ProfileProvider::getOnlineProfiles)
                .ifPresent(onlineProfiles -> onlineProfiles.forEach(onlineProfile -> onlineProfile.getPlayer().closeInventory()));

        coreComponentLoader.getOptional(Saver.class).ifPresent(Saver::end);
        coreComponentLoader.getOptional(Compatibility.class).ifPresent(Compatibility::disable);
        coreComponentLoader.getOptional(Connector.class).ifPresent(connector -> connector.getDatabase().closeConnection());
        coreComponentLoader.getOptional(PlayerHider.class).ifPresent(PlayerHider::stop);
        coreComponentLoader.getOptional(RPGMenu.class).ifPresent(RPGMenu::onDisable);

        log.info("BetonQuest successfully disabled!");
    }

    /**
     * Adds a Processor to re-/load data on BetonQuest re-/load.
     *
     * @param processor the processor to register
     */
    public void addProcessor(final QuestProcessor<?, ?> processor) {
        coreComponentLoader.get(ProcessorDataLoader.class).addProcessor(processor);
    }

    /**
     * Returns the {@link CoreComponentLoader} instance.
     *
     * @return the {@link CoreComponentLoader} instance
     */
    public CoreComponentLoader getComponentLoader() {
        return coreComponentLoader;
    }

    /**
     * Returns the {@link BetonQuestLoggerFactory} instance.
     *
     * @return the {@link BetonQuestLoggerFactory} instance
     */
    public BetonQuestLoggerFactory getLoggerFactory() {
        return coreComponentLoader.get(BetonQuestLoggerFactory.class);
    }

    /**
     * Get the plugin configuration file.
     *
     * @return config file
     */
    public ConfigAccessor getPluginConfig() {
        return coreComponentLoader.get(FileConfigAccessor.class);
    }

    /**
     * Returns the {@link BetonQuestApi} instance.
     *
     * @return the {@link BetonQuestApi} instance
     */
    public BetonQuestApi getBetonQuestApi() {
        return coreComponentLoader.get(BetonQuestApi.class);
    }

    /**
     * Get the plugin messages provider.
     *
     * @return plugin messages provider
     */
    public PluginMessage getPluginMessage() {
        return coreComponentLoader.get(PluginMessage.class);
    }

    /**
     * Returns the {@link ProfileProvider} instance.
     *
     * @return the {@link ProfileProvider} instance
     */
    public ProfileProvider getProfileProvider() {
        return coreComponentLoader.get(ProfileProvider.class);
    }

    /**
     * Returns the {@link QuestPackageManager} instance.
     *
     * @return the {@link QuestPackageManager} instance
     */
    public QuestPackageManager getQuestPackageManager() {
        return coreComponentLoader.get(QuestPackageManager.class);
    }

    /**
     * Returns the connector for the database.
     *
     * @return Connector instance
     */
    public Connector getDBConnector() {
        return coreComponentLoader.get(Connector.class);
    }

    /**
     * Checks if MySQL is used or not.
     *
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return coreComponentLoader.get(DatabaseComponent.class).usesMySQL();
    }

    /**
     * Returns the {@link Saver} instance used by BetonQuest.
     *
     * @return the database saver
     */
    public Saver getSaver() {
        return coreComponentLoader.get(Saver.class);
    }

    /**
     * Gets the stored player data.
     *
     * @return storage for currently loaded player data
     */
    public PlayerDataStorage getPlayerDataStorage() {
        return coreComponentLoader.get(PlayerDataStorage.class);
    }

    /**
     * Get the colors used in conversations.
     *
     * @return the colors used in conversations
     */
    public ConversationColors getConversationColors() {
        return coreComponentLoader.get(ConversationColors.class);
    }

    /**
     * Get the Compatibility to add plugins and initialize it.
     *
     * @return the compatibility
     */
    protected Compatibility getCompatibility() {
        return coreComponentLoader.get(Compatibility.class);
    }
}
