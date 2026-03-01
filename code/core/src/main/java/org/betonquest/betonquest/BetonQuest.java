package org.betonquest.betonquest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.QuestManager;
import org.betonquest.betonquest.config.patcher.migration.Migrator;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.AsyncSaver;
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
import org.betonquest.betonquest.kernel.component.NotificationsComponent;
import org.betonquest.betonquest.kernel.component.NpcsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
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
import java.util.Set;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "NullAway.Init"})
public class BetonQuest extends JavaPlugin implements LanguageProvider {

    /**
     * All of those classes have to exist to determine the server software to be Paper.
     */
    public static final Set<String> PAPER_IDENTIFYING_CLASSES =
            Set.of("com.destroystokyo.paper.PaperConfig", "io.papermc.paper.configuration.Configuration");

    /**
     * The BetonQuest Plugin instance.
     */
    private static BetonQuest instance;

    /**
     * Factory to create new class-specific loggers.
     */
    private BetonQuestLoggerFactory loggerFactory;

    /**
     * The custom logger for the plugin.
     */
    private BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private FileConfigAccessor config;

    /**
     * The used Connector for the Database.
     */
    private Connector connector;

    /**
     * The database saver for Quest Data.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private AsyncSaver saver;

    /**
     * The profile provider instance.
     */
    private ProfileProvider profileProvider;

    /**
     * The quest manager instance.
     */
    private QuestManager questManager;

    /**
     * The colors for conversations.
     */
    private ConversationColors conversationColors;

    /**
     * The Compatibility instance for hooking into other plugins.
     */
    private Compatibility compatibility;

    /**
     * The PlayerDataStorage instance.
     */
    private PlayerDataStorage playerDataStorage;

    /**
     * The betonQuestApi instance.
     */
    private BetonQuestApi betonQuestApi;

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

    @SuppressWarnings("PMD.DoNotUseThreads")
    @Override
    public void onEnable() {
        instance = this;

        this.loggerFactory = new CachingBetonQuestLoggerFactory(new DefaultBetonQuestLoggerFactory());
        if (!isPaper()) {
            throw new IllegalStateException("Only Paper is supported!");
        }

        final DefaultCoreComponentLoader coreComponentLoader = new DefaultCoreComponentLoader(loggerFactory.create(DefaultCoreComponentLoader.class));
        coreComponentLoader.init(BetonQuestLoggerFactory.class, loggerFactory);
        this.coreComponentLoader = coreComponentLoader;
        initPluginDependencies(coreComponentLoader);
        registerComponents(coreComponentLoader);
        registerFeatures(coreComponentLoader);
        registerTypesComponents(coreComponentLoader);
        this.coreComponentLoader.load();

        this.profileProvider = coreComponentLoader.get(ProfileProvider.class);
        this.log = loggerFactory.create(this);
        this.config = coreComponentLoader.get(FileConfigAccessor.class);
        this.questManager = coreComponentLoader.get(QuestManager.class);
        this.betonQuestApi = coreComponentLoader.get(BetonQuestApi.class);
        this.compatibility = coreComponentLoader.get(Compatibility.class);
        this.playerDataStorage = coreComponentLoader.get(PlayerDataStorage.class);
        this.saver = coreComponentLoader.get(AsyncSaver.class);
        this.connector = coreComponentLoader.get(Connector.class);
        this.conversationColors = coreComponentLoader.get(ConversationColors.class);

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
                new DatabaseComponent(),
                new AsyncSaverComponent(),
                new GlobalDataComponent(),
                new FontRegistryComponent(),
                new ListenersComponent(),
                new PlayerHiderComponent(),
                new UpdaterComponent(this.getFile()),
                new ConversationColorsComponent(),
                new ExecutionCacheComponent(),
                new DataLoaderComponent(),
                new PostEnableComponent()
        ).forEach(coreComponentLoader::register);
    }

    private boolean isPaper() {
        return PAPER_IDENTIFYING_CLASSES.stream().anyMatch(this::testClass);
    }

    private boolean testClass(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException exception) {
            return false;
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void onDisable() {
        try {
            coreComponentLoader.get(ActionScheduling.class).clear();
        } catch (final Exception ignored) {
            // Empty
        }

        if (profileProvider != null) {
            for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
                final Conversation conv = coreComponentLoader.get(ConversationProcessor.class).getActiveConversation(onlineProfile);
                if (conv != null) {
                    conv.suspend();
                }
                onlineProfile.getPlayer().closeInventory();
            }
        }

        if (saver != null) {
            saver.end();
        }
        if (compatibility != null) {
            compatibility.disable();
        }
        if (connector != null) {
            connector.getDatabase().closeConnection();
        }

        coreComponentLoader.get(PlayerHider.class).stop();

        try {
            coreComponentLoader.get(RPGMenu.class).onDisable();
        } catch (final Exception ignored) {
            // Empty
        }
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
        return loggerFactory;
    }

    /**
     * Get the plugin configuration file.
     *
     * @return config file
     */
    public ConfigAccessor getPluginConfig() {
        return config;
    }

    @Override
    public String getDefaultLanguage() {
        return coreComponentLoader.get(LanguageProvider.class).getDefaultLanguage();
    }

    /**
     * Returns the {@link BetonQuestApi} instance.
     *
     * @return the {@link BetonQuestApi} instance
     */
    public BetonQuestApi getBetonQuestApi() {
        return betonQuestApi;
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
        return profileProvider;
    }

    /**
     * Returns the {@link QuestPackageManager} instance.
     *
     * @return the {@link QuestPackageManager} instance
     */
    public QuestPackageManager getQuestPackageManager() {
        return questManager;
    }

    /**
     * Returns the connector for the database.
     *
     * @return Connector instance
     */
    public Connector getDBConnector() {
        return connector;
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
        return saver;
    }

    /**
     * Gets the stored player data.
     *
     * @return storage for currently loaded player data
     */
    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    /**
     * Get the colors used in conversations.
     *
     * @return the colors used in conversations
     */
    public ConversationColors getConversationColors() {
        return conversationColors;
    }

    /**
     * Get the registry for fonts to calculate width of text.
     *
     * @return the font registry
     */
    public FontRegistry getFontRegistry() {
        return coreComponentLoader.get(FontRegistry.class);
    }

    /**
     * Get the Compatibility to add plugins and initialize it.
     *
     * @return the compatibility
     */
    protected Compatibility getCompatibility() {
        return compatibility;
    }
}
