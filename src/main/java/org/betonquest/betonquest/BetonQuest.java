package org.betonquest.betonquest;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.bstats.BStatsMetrics;
import org.betonquest.betonquest.command.BackpackCommand;
import org.betonquest.betonquest.command.CancelQuestCommand;
import org.betonquest.betonquest.command.CompassCommand;
import org.betonquest.betonquest.command.JournalCommand;
import org.betonquest.betonquest.command.LangCommand;
import org.betonquest.betonquest.command.QuestCommand;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.DefaultConfigurationFileFactory;
import org.betonquest.betonquest.config.patcher.migration.Migrator;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.AsyncSaver;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.HandlerFactory;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.logger.handler.chat.ChatHandler;
import org.betonquest.betonquest.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.betonquest.betonquest.quest.registry.CoreQuestTypes;
import org.betonquest.betonquest.quest.registry.QuestRegistry;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.feature.CoreFeatureFactories;
import org.betonquest.betonquest.quest.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.quest.registry.processor.NpcProcessor;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.util.PlayerConverter;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.java.JREVersionPrinter;
import org.betonquest.betonquest.web.DownloadSource;
import org.betonquest.betonquest.web.TempFileDownloadSource;
import org.betonquest.betonquest.web.WebContentSource;
import org.betonquest.betonquest.web.WebDownloadSource;
import org.betonquest.betonquest.web.updater.UpdateDownloader;
import org.betonquest.betonquest.web.updater.UpdateSourceHandler;
import org.betonquest.betonquest.web.updater.Updater;
import org.betonquest.betonquest.web.updater.UpdaterConfig;
import org.betonquest.betonquest.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.web.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.web.updater.source.implementations.GitHubReleaseSource;
import org.betonquest.betonquest.web.updater.source.implementations.NexusReleaseAndDevelopmentSource;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.InstantSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields", "NullAway.Init"})
public class BetonQuest extends JavaPlugin {
    /**
     * BStats Plugin id.
     */
    private static final int BSTATS_METRICS_ID = 551;

    /**
     * The indicator for dev versions.
     */
    private static final String DEV_INDICATOR = "DEV";

    /**
     * The File where last executions should be cached.
     */
    private static final String CACHE_FILE = ".cache/schedules.yml";

    /**
     * The BetonQuest Plugin instance.
     */
    private static BetonQuest instance;

    /**
     * Stores the loaded PlayerData.
     */
    private PlayerDataStorage playerDataStorage;

    /**
     * Stores Conditions, Events, Objectives, Variables, Conversations and Cancelers.
     */
    private QuestRegistry questRegistry;

    /**
     * Registry for quest core elements.
     */
    private QuestTypeRegistries questTypeRegistries;

    /**
     * Stores Registry for ConvIO, Interceptor, NotifyIO and EventScheduling.
     */
    private FeatureRegistries featureRegistries;

    /**
     * Factory to create new class specific loggers.
     */
    private BetonQuestLoggerFactory loggerFactory;

    /**
     * Factory to create new file accessors.
     */
    private ConfigAccessorFactory configAccessorFactory;

    /**
     * Factory to create new Configuration Files.
     */
    private ConfigurationFileFactory configurationFileFactory;

    /**
     * The custom logger for the plugin.
     */
    private BetonQuestLogger log;

    /**
     * The plugin tag used for command feedback.
     */
    private String pluginTag;

    /**
     * The plugin configuration file.
     */
    private ConfigurationFile config;

    /**
     * The adventure instance.
     */
    private BukkitAudiences adventure;

    /**
     * The used Database.
     */
    private Database database;

    /**
     * If MySQL is used.
     */
    private boolean usesMySQL;

    /**
     * The database saver for Quest Data.
     */
    @SuppressWarnings("PMD.DoNotUseThreads")
    private AsyncSaver saver;

    /**
     * The plugin updater.
     */
    private Updater updater;

    /**
     * The Global Quest Data.
     */
    private GlobalData globalData;

    /**
     * The Player Hider instance.
     */
    private PlayerHider playerHider;

    /**
     * The RPG Menu instance.
     */
    private RPGMenu rpgMenu;

    /**
     * Quest Type API.
     */
    private QuestTypeAPI questTypeAPI;

    /**
     * Feature API.
     */
    private FeatureAPI featureAPI;

    /**
     * Cache for event schedulers, holding the last execution of an event.
     */
    private LastExecutionCache lastExecutionCache;

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

    /**
     * Get the BetonQuest logger factory.
     *
     * @return The logger factory.
     */
    public BetonQuestLoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * Get the ConfigAccessor factory.
     *
     * @return The ConfigAccessor factory.
     */
    public ConfigAccessorFactory getConfigAccessorFactory() {
        return configAccessorFactory;
    }

    /**
     * Get the ConfigurationFile factory.
     *
     * @return The ConfigurationFile factory.
     */
    public ConfigurationFileFactory getConfigurationFileFactory() {
        return configurationFileFactory;
    }

    /**
     * Get the adventure instance.
     *
     * @return The adventure instance.
     */
    public BukkitAudiences getAdventure() {
        return adventure;
    }

    /**
     * Get the RPG Menu instance.
     *
     * @return The RPG Menu instance.
     */
    public RPGMenu getRpgMenu() {
        return rpgMenu;
    }

    /**
     * Get the plugin configuration file.
     *
     * @return config file
     */
    public ConfigurationFile getPluginConfig() {
        return config;
    }

    /**
     * Get the plugin tag used for command feedback.
     *
     * @return plugin tag
     */
    public String getPluginTag() {
        return pluginTag;
    }

    /**
     * Ensures that the given event is called on the main server thread.
     *
     * @param event the event to call
     */
    public void callSyncBukkitEvent(final Event event) {
        if (getServer().isPrimaryThread()) {
            getServer().getPluginManager().callEvent(event);
        } else {
            getServer().getScheduler().runTask(this, () -> getServer().getPluginManager().callEvent(event));
        }
    }

    private <T> T registerAndGetService(final Class<T> clazz, final T service) {
        final ServicesManager servicesManager = getServer().getServicesManager();
        servicesManager.register(clazz, service, this, ServicePriority.Lowest);
        return servicesManager.load(clazz);
    }

    @SuppressWarnings({"PMD.NcssCount", "PMD.DoNotUseThreads"})
    @Override
    public void onEnable() {
        instance = this;

        this.loggerFactory = registerAndGetService(BetonQuestLoggerFactory.class, new CachingBetonQuestLoggerFactory(new DefaultBetonQuestLoggerFactory()));
        this.configAccessorFactory = registerAndGetService(ConfigAccessorFactory.class, new DefaultConfigAccessorFactory());
        this.configurationFileFactory = registerAndGetService(ConfigurationFileFactory.class, new DefaultConfigurationFileFactory(
                loggerFactory, loggerFactory.create(DefaultConfigurationFileFactory.class), configAccessorFactory));

        this.log = loggerFactory.create(this);
        pluginTag = ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + getDescription().getName() + ChatColor.GRAY + "]" + ChatColor.RESET + " ";

        final JREVersionPrinter jreVersionPrinter = new JREVersionPrinter();
        final String jreInfo = jreVersionPrinter.getMessage();
        log.info(jreInfo);

        migratePackages();

        try {
            config = configurationFileFactory.create(new File(getDataFolder(), "config.yml"), this, "config.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.error("Could not load the config.yml file!", e);
            return;
        }

        final ConfigAccessor menuConfigAccessor;
        try {
            menuConfigAccessor = configAccessorFactory.create(new File(getDataFolder(), "menuConfig.yml"), this, "menuConfig.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.error("Could not load the menuConfig.yml file!", e);
            return;
        }

        final HistoryHandler debugHistoryHandler = HandlerFactory.createHistoryHandler(loggerFactory, this,
                this.getServer().getScheduler(), config, new File(getDataFolder(), "/logs"), InstantSource.system());
        registerLogHandler(getServer(), debugHistoryHandler);
        adventure = BukkitAudiences.create(this);
        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final ChatHandler chatHandler = HandlerFactory.createChatHandler(this, receiverSelector, adventure);
        registerLogHandler(getServer(), chatHandler);

        final String version = getDescription().getVersion();
        log.debug("BetonQuest " + version + " is starting...");
        log.debug(jreInfo);

        Config.setup(this, config);
        Notify.load(config);

        setupDatabase();

        saver = new AsyncSaver(loggerFactory.create(AsyncSaver.class, "Database"));
        saver.start();
        Backup.loadDatabaseFromBackup(configAccessorFactory);

        globalData = new GlobalData(loggerFactory.create(GlobalData.class), saver);

        playerDataStorage = new PlayerDataStorage(loggerFactory, loggerFactory.create(PlayerDataStorage.class));

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new QuestItemHandler(playerDataStorage), this);

        final ConfigAccessor cache;
        try {
            final Path cacheFile = new File(getDataFolder(), CACHE_FILE).toPath();
            if (!Files.exists(cacheFile)) {
                Files.createDirectories(Optional.ofNullable(cacheFile.getParent()).orElseThrow());
                Files.createFile(cacheFile);
            }
            cache = configAccessorFactory.create(cacheFile.toFile());
        } catch (final IOException | InvalidConfigurationException e) {
            log.error("Error while loading schedule cache: " + e.getMessage(), e);
            return;
        }
        lastExecutionCache = new LastExecutionCache(loggerFactory.create(LastExecutionCache.class, "Cache"), cache);

        new GlobalObjectives();

        pluginManager.registerEvents(new CombatTagger(config.getInt("combat_delay")), this);

        ConversationColors.loadColors();

        pluginManager.registerEvents(new MobKillListener(), this);

        pluginManager.registerEvents(new CustomDropListener(loggerFactory.create(CustomDropListener.class)), this);

        registerCommands(receiverSelector, debugHistoryHandler);

        questTypeRegistries = QuestTypeRegistries.create(loggerFactory);
        featureRegistries = FeatureRegistries.create(loggerFactory);

        questRegistry = new QuestRegistry(loggerFactory.create(QuestRegistry.class), loggerFactory, this,
                featureRegistries, questTypeRegistries);

        questTypeAPI = new QuestTypeAPI(questRegistry);
        featureAPI = new FeatureAPI(questRegistry);
        pluginManager.registerEvents(new JoinQuitListener(loggerFactory, questTypeAPI, playerDataStorage), this);

        new CoreQuestTypes(loggerFactory, getServer(), getServer().getScheduler(), this,
                questTypeAPI, questRegistry.variables(), globalData, playerDataStorage).register(questTypeRegistries);

        new CoreFeatureFactories(loggerFactory, lastExecutionCache, questTypeAPI).register(featureRegistries);

        new Compatibility(this, loggerFactory.create(Compatibility.class));

        // schedule quest data loading on the first tick, so all other
        // plugins can register their types
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Compatibility.postHook();
            loadData();
            playerDataStorage.initProfiles(PlayerConverter.getOnlineProfiles());

            try {
                playerHider = new PlayerHider(this, questTypeAPI);
            } catch (final QuestException e) {
                log.error("Could not start PlayerHider! " + e.getMessage(), e);
            }
        });

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            log.warn("Could not disable /betonquestanswer logging", e);
        }

        new BStatsMetrics(this, new Metrics(this, BSTATS_METRICS_ID), questRegistry.metricsSupplier());

        setupUpdater();

        rpgMenu = new RPGMenu(loggerFactory.create(RPGMenu.class), loggerFactory, menuConfigAccessor);

        PaperLib.suggestPaper(this);
        log.info("BetonQuest successfully enabled!");
    }

    private void setupDatabase() {
        final boolean mySQLEnabled = config.getBoolean("mysql.enabled", true);
        if (mySQLEnabled) {
            log.debug("Connecting to MySQL database");
            this.database = new MySQL(loggerFactory.create(MySQL.class, "Database"), this,
                    config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.base"),
                    config.getString("mysql.user"),
                    config.getString("mysql.pass"));
            if (database.getConnection() != null) {
                usesMySQL = true;
                log.info("Successfully connected to MySQL database!");
            }
        }
        if (!mySQLEnabled || !usesMySQL) {
            this.database = new SQLite(loggerFactory.create(SQLite.class, "Database"), this, "database.db");
            if (mySQLEnabled) {
                log.warn("No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                log.info("Using SQLite for storing data!");
            }
        }

        database.createTables();
    }

    private void registerCommands(final AccumulatingReceiverSelector receiverSelector, final HistoryHandler debugHistoryHandler) {
        final QuestCommand questCommand = new QuestCommand(loggerFactory, loggerFactory.create(QuestCommand.class),
                configAccessorFactory, adventure, new PlayerLogWatcher(receiverSelector), debugHistoryHandler,
                this, playerDataStorage);
        getCommand("betonquest").setExecutor(questCommand);
        getCommand("betonquest").setTabCompleter(questCommand);
        getCommand("journal").setExecutor(new JournalCommand(playerDataStorage));
        getCommand("backpack").setExecutor(new BackpackCommand(loggerFactory.create(BackpackCommand.class)));
        getCommand("cancelquest").setExecutor(new CancelQuestCommand());
        getCommand("compass").setExecutor(new CompassCommand());
        final LangCommand langCommand = new LangCommand(loggerFactory.create(LangCommand.class), this, playerDataStorage);
        getCommand("questlang").setExecutor(langCommand);
        getCommand("questlang").setTabCompleter(langCommand);
    }

    private void migratePackages() {
        try {
            new Migrator().migrate();
        } catch (final IOException e) {
            log.error("There was an exception while migrating from a previous version! Reason: " + e.getMessage(), e);
        }
    }

    private void setupUpdater() {
        final File updateFolder = getServer().getUpdateFolderFile();
        final File file = new File(updateFolder, this.getFile().getName());
        final DownloadSource downloadSource = new TempFileDownloadSource(new WebDownloadSource());
        final UpdateDownloader updateDownloader = new UpdateDownloader(downloadSource, file);

        final NexusReleaseAndDevelopmentSource nexusReleaseAndDevelopmentSource = new NexusReleaseAndDevelopmentSource(
                "https://nexus.betonquest.org/", new WebContentSource());
        final GitHubReleaseSource gitHubReleaseSource = new GitHubReleaseSource(
                "https://api.github.com/repos/BetonQuest/BetonQuest",
                new WebContentSource(GitHubReleaseSource.HTTP_CODE_HANDLER));
        final List<ReleaseUpdateSource> releaseHandlers = List.of(nexusReleaseAndDevelopmentSource, gitHubReleaseSource);
        final List<DevelopmentUpdateSource> developmentHandlers = List.of(nexusReleaseAndDevelopmentSource);
        final UpdateSourceHandler updateSourceHandler = new UpdateSourceHandler(loggerFactory.create(UpdateSourceHandler.class),
                releaseHandlers, developmentHandlers);

        final Version pluginVersion = new Version(this.getDescription().getVersion());
        final UpdaterConfig updaterConfig = new UpdaterConfig(loggerFactory.create(UpdaterConfig.class), config, pluginVersion, DEV_INDICATOR);
        updater = new Updater(loggerFactory.create(Updater.class), updaterConfig, pluginVersion, updateSourceHandler, updateDownloader,
                this, getServer().getScheduler(), InstantSource.system());
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    private void registerLogHandler(final Server server, final Handler handler) {
        final java.util.logging.Logger serverLogger = server.getLogger().getParent();
        serverLogger.addHandler(handler);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serverLogger.removeHandler(handler);
            handler.close();
        }));
    }

    /**
     * Loads QuestPackages and refreshes player objectives.
     *
     * @see QuestRegistry#loadData(Collection)
     */
    public void loadData() {
        questRegistry.loadData(Config.getPackages().values());
        playerDataStorage.startObjectives();
        rpgMenu.reloadData();
        Bukkit.getPluginManager().callEvent(new LoadDataEvent());
    }

    /**
     * Reloads the plugin.
     */
    public void reload() {
        // reload the configuration
        log.debug("Reloading configuration");
        try {
            config.reload();
        } catch (final IOException e) {
            log.warn("Could not reload config! " + e.getMessage(), e);
        }
        Config.setup(this, config);
        Notify.load(config);
        lastExecutionCache.reload();

        // reload updater settings
        getUpdater().search();
        // stop current global locations listener
        // and start new one with reloaded configs
        log.debug("Restarting global locations");
        new GlobalObjectives();
        ConversationColors.loadColors();
        Compatibility.reload();
        // load all events, conditions, objectives, conversations etc.
        loadData();
        playerDataStorage.reloadProfiles(PlayerConverter.getOnlineProfiles());

        if (playerHider != null) {
            playerHider.stop();
        }
        try {
            playerHider = new PlayerHider(this, questTypeAPI);
        } catch (final QuestException e) {
            log.error("Could not start PlayerHider! " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    @Override
    public void onDisable() {
        if (questRegistry != null) {
            questRegistry.stopAllEventSchedules();
        }
        // suspend all conversations
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            final Conversation conv = Conversation.getConversation(onlineProfile);
            if (conv != null) {
                conv.suspend();
            }
            onlineProfile.getPlayer().closeInventory();
        }
        // cancel database saver
        if (saver != null) {
            saver.end();
        }
        Compatibility.disable();
        if (database != null) {
            database.closeConnection();
        }
        if (playerHider != null) {
            playerHider.stop();
        }

        // done
        log.info("BetonQuest successfully disabled!");

        if (this.adventure != null) {
            this.adventure.close();
        }

        if (rpgMenu != null) {
            rpgMenu.onDisable();
        }
    }

    /**
     * Returns the Quest Type API.
     *
     * @return the api for Quest Type logic
     */
    public QuestTypeAPI getQuestTypeAPI() {
        return questTypeAPI;
    }

    /**
     * Returns the Feature API.
     *
     * @return the api for feature logic
     */
    public FeatureAPI getFeatureAPI() {
        return featureAPI;
    }

    /**
     * Returns the database instance.
     *
     * @return Database instance
     */
    public Database getDB() {
        return database;
    }

    public Updater getUpdater() {
        return updater;
    }

    /**
     * Checks if MySQL is used or not.
     *
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return usesMySQL;
    }

    /**
     * Retrieves GlobalData object which handles all global tags and points.
     *
     * @return GlobalData object
     */
    public GlobalData getGlobalData() {
        return globalData;
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
     * Gets the QuestRegistry holding the core Quest types.
     *
     * @return registry holding Conditions and Events
     */
    public QuestTypeRegistries getQuestRegistries() {
        return questTypeRegistries;
    }

    /**
     * Gets the Registries holding other types.
     *
     * @return registry holding ConvIO, Interceptor, ...
     */
    public FeatureRegistries getFeatureRegistries() {
        return featureRegistries;
    }

    /**
     * Get the VariableProcessor instance.
     *
     * @return the VariableProcessor to resolve variables
     */
    public VariableProcessor getVariableProcessor() {
        return questRegistry.variables();
    }

    /**
     * Get the NpcProcessor instance.
     *
     * @return the NpcProcessor to start npc conversations
     */
    public NpcProcessor getNpcProcessor() {
        return questRegistry.npcs();
    }
}
