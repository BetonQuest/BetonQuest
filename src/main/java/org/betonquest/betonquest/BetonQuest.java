package org.betonquest.betonquest;

import com.google.common.base.Suppliers;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.key.Key;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.bukkit.event.LoadDataEvent;
import org.betonquest.betonquest.api.common.component.font.DefaultFont;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.feature.FeatureRegistries;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.bstats.BStatsMetrics;
import org.betonquest.betonquest.command.BackpackCommand;
import org.betonquest.betonquest.command.CancelQuestCommand;
import org.betonquest.betonquest.command.CompassCommand;
import org.betonquest.betonquest.command.JournalCommand;
import org.betonquest.betonquest.command.LangCommand;
import org.betonquest.betonquest.command.QuestCommand;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.QuestManager;
import org.betonquest.betonquest.config.patcher.migration.Migrator;
import org.betonquest.betonquest.config.patcher.migration.QuestMigrator;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.AsyncSaver;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.feature.CoreFeatureFactories;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.kernel.processor.CoreQuestRegistry;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.kernel.processor.QuestRegistry;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.BaseFeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.BaseQuestTypeRegistries;
import org.betonquest.betonquest.listener.CustomDropListener;
import org.betonquest.betonquest.listener.JoinQuitListener;
import org.betonquest.betonquest.listener.MobKillListener;
import org.betonquest.betonquest.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.logger.HandlerFactory;
import org.betonquest.betonquest.logger.PlayerLogWatcher;
import org.betonquest.betonquest.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.logger.handler.chat.ChatHandler;
import org.betonquest.betonquest.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.playerhider.PlayerHider;
import org.betonquest.betonquest.profile.UUIDProfileProvider;
import org.betonquest.betonquest.quest.CoreQuestTypes;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.text.DecidingTextParser;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.betonquest.betonquest.text.TagTextParserDecider;
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
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
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
import java.util.logging.Level;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields", "NullAway.Init"})
public class BetonQuest extends JavaPlugin implements BetonQuestApi, LanguageProvider {
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
    private BaseQuestTypeRegistries questTypeRegistries;

    /**
     * Stores Registry for ConvIO, Interceptor, NotifyIO and EventScheduling.
     */
    private BaseFeatureRegistries featureRegistries;

    /**
     * Factory to create new class specific loggers.
     */
    private BetonQuestLoggerFactory loggerFactory;

    /**
     * Factory to create new file accessors.
     */
    private ConfigAccessorFactory configAccessorFactory;

    /**
     * The custom logger for the plugin.
     */
    private BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private FileConfigAccessor config;

    /**
     * The default language from the config.
     */
    private String defaultLanguage;

    /**
     * The text parser.
     */
    private TextParser textParser;

    /**
     * The plugin messages provider.
     */
    private PluginMessage pluginMessage;

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
     * Cache for event schedulers, holding the last execution of an event.
     */
    private LastExecutionCache lastExecutionCache;

    /**
     * The profile provider instance.
     */
    private ProfileProvider profileProvider;

    /**
     * The quest manager instance.
     */
    private QuestManager questManager;

    /**
     * The registry for fonts to calculate width of text.
     */
    private FontRegistry fontRegistry;

    /**
     * The colors for conversations.
     */
    private ConversationColors conversationColors;

    /**
     * The Compatibility instance for hooking into other plugins.
     */
    private Compatibility compatibility;

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
        this.log = loggerFactory.create(this);
        if (!PaperLib.isPaper()) {
            PaperLib.suggestPaper(this, Level.WARNING);
            log.warn("Only Paper is supported! Disabling BetonQuest...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.configAccessorFactory = registerAndGetService(ConfigAccessorFactory.class, new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class)));
        this.profileProvider = registerAndGetService(ProfileProvider.class, new UUIDProfileProvider(getServer()));

        final JREVersionPrinter jreVersionPrinter = new JREVersionPrinter();
        final String jreInfo = jreVersionPrinter.getMessage();
        log.info(jreInfo);

        migrate();

        try {
            config = configAccessorFactory.createPatching(new File(getDataFolder(), "config.yml"), this, "config.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.error("Could not load the config.yml file!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        defaultLanguage = config.getString("language", "en-US");

        final HistoryHandler debugHistoryHandler = HandlerFactory.createHistoryHandler(loggerFactory, this,
                this.getServer().getScheduler(), config, new File(getDataFolder(), "/logs"), InstantSource.system());
        registerLogHandler(getServer(), debugHistoryHandler);
        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final ChatHandler chatHandler = HandlerFactory.createChatHandler(this, getServer(), receiverSelector);
        registerLogHandler(getServer(), chatHandler);

        final String version = getDescription().getVersion();
        log.debug("BetonQuest " + version + " is starting...");
        log.debug(jreInfo);

        questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), configAccessorFactory,
                getDataFolder(), new QuestMigrator(loggerFactory.create(QuestMigrator.class), getDescription()));
        Notify.load(config, questManager.getPackages().values());

        setupDatabase();

        saver = new AsyncSaver(loggerFactory.create(AsyncSaver.class, "Database"), config);
        saver.start();
        new Backup(loggerFactory.create(Backup.class), configAccessorFactory, instance.getDataFolder(), new Connector())
                .loadDatabaseFromBackup();

        globalData = new GlobalData(loggerFactory.create(GlobalData.class), saver);

        final FileConfigAccessor cache;
        try {
            final Path cacheFile = new File(getDataFolder(), CACHE_FILE).toPath();
            if (!Files.exists(cacheFile)) {
                Files.createDirectories(Optional.ofNullable(cacheFile.getParent()).orElseThrow());
                Files.createFile(cacheFile);
            }
            cache = configAccessorFactory.create(cacheFile.toFile());
        } catch (final IOException | InvalidConfigurationException e) {
            log.error("Error while loading schedule cache: " + e.getMessage(), e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        lastExecutionCache = new LastExecutionCache(loggerFactory.create(LastExecutionCache.class, "Cache"), cache);

        questTypeRegistries = BaseQuestTypeRegistries.create(loggerFactory, this);
        final CoreQuestRegistry coreQuestRegistry = new CoreQuestRegistry(loggerFactory, questManager, questTypeRegistries,
                getServer().getPluginManager(), this);

        final PlayerDataFactory playerDataFactory = new PlayerDataFactory(loggerFactory, questManager, saver, getServer(),
                coreQuestRegistry, Suppliers.memoize(() -> new JournalFactory(pluginMessage, coreQuestRegistry, questRegistry,
                config, textParser, fontRegistry)));
        playerDataStorage = new PlayerDataStorage(loggerFactory, loggerFactory.create(PlayerDataStorage.class), config,
                playerDataFactory, coreQuestRegistry.objectives(), profileProvider);

        featureRegistries = BaseFeatureRegistries.create(loggerFactory);

        final String defaultParser = config.getString("text_parser", "legacyminimessage");
        textParser = new DecidingTextParser(featureRegistries.textParser(), new TagTextParserDecider(defaultParser));
        try {
            pluginMessage = new PluginMessage(this, coreQuestRegistry.variables(), playerDataStorage,
                    textParser, configAccessorFactory, this);
            for (final String language : pluginMessage.getLanguages()) {
                log.debug("Loaded " + language + " language");
            }
        } catch (final QuestException e) {
            log.error("Could not load the plugin messages!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final ParsedSectionTextCreator textCreator = new ParsedSectionTextCreator(textParser, playerDataStorage,
                this, coreQuestRegistry.variables());
        questRegistry = QuestRegistry.create(loggerFactory.create(QuestRegistry.class), loggerFactory, this,
                coreQuestRegistry, featureRegistries, pluginMessage, textCreator, profileProvider, playerDataStorage);

        setupUpdater();
        registerListener(coreQuestRegistry);

        new CoreQuestTypes(loggerFactory, getServer(), getServer().getScheduler(), this,
                coreQuestRegistry, pluginMessage, coreQuestRegistry.variables(), globalData, playerDataStorage,
                profileProvider, this, playerDataFactory)
                .register(questTypeRegistries);

        conversationColors = new ConversationColors(textParser, config);

        final Key defaultkey = Key.key("default");
        fontRegistry = new FontRegistry(defaultkey);
        fontRegistry.registerFont(defaultkey, new DefaultFont());

        new CoreFeatureFactories(loggerFactory, questManager, lastExecutionCache, coreQuestRegistry, config, conversationColors,
                textParser, fontRegistry)
                .register(featureRegistries);

        try {
            conversationColors.load();
        } catch (final QuestException e) {
            log.warn("Could not load conversation colors! " + e.getMessage(), e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        compatibility = new Compatibility(loggerFactory.create(Compatibility.class), this, config, version);
        Bukkit.getPluginManager().registerEvents(compatibility, this);

        registerCommands(receiverSelector, debugHistoryHandler, playerDataFactory);

        // schedule quest data loading on the first tick, so all other
        // plugins can register their types
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            compatibility.postHook();
            loadData();
            playerDataStorage.initProfiles(profileProvider.getOnlineProfiles(), pluginMessage);

            try {
                playerHider = new PlayerHider(this, this, getVariableProcessor(), profileProvider, config);
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

        new BStatsMetrics(this, new Metrics(this, BSTATS_METRICS_ID), questRegistry.metricsSupplier(), compatibility);

        rpgMenu = new RPGMenu(loggerFactory.create(RPGMenu.class), loggerFactory, questManager, config,
                coreQuestRegistry.variables(), pluginMessage, textCreator, coreQuestRegistry, questRegistry,
                profileProvider);

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

    private void registerListener(final CoreQuestRegistry coreQuestRegistry) {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        List.of(
                new CombatTagger(profileProvider, config.getInt("conversation.damage.combat_delay")),
                new MobKillListener(profileProvider),
                new CustomDropListener(loggerFactory.create(CustomDropListener.class), questManager, this, questRegistry),
                new QuestItemHandler(config, playerDataStorage, profileProvider),
                new JoinQuitListener(loggerFactory, config, coreQuestRegistry.objectives(), playerDataStorage,
                        pluginMessage, profileProvider, updater)
        ).forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    private void registerCommands(final AccumulatingReceiverSelector receiverSelector, final HistoryHandler debugHistoryHandler,
                                  final PlayerDataFactory playerDataFactory) {
        final QuestCommand questCommand = new QuestCommand(loggerFactory, loggerFactory.create(QuestCommand.class),
                configAccessorFactory, new PlayerLogWatcher(receiverSelector), debugHistoryHandler,
                this, playerDataStorage, profileProvider, playerDataFactory, pluginMessage, config, compatibility);
        getCommand("betonquest").setExecutor(questCommand);
        getCommand("betonquest").setTabCompleter(questCommand);
        getCommand("journal").setExecutor(new JournalCommand(playerDataStorage, profileProvider));
        getCommand("backpack").setExecutor(new BackpackCommand(loggerFactory.create(BackpackCommand.class), config, pluginMessage, profileProvider));
        getCommand("cancelquest").setExecutor(new CancelQuestCommand(config, pluginMessage, profileProvider));
        getCommand("compass").setExecutor(new CompassCommand(config, pluginMessage, profileProvider));
        final LangCommand langCommand = new LangCommand(loggerFactory.create(LangCommand.class), playerDataStorage, pluginMessage, profileProvider, this);
        getCommand("questlang").setExecutor(langCommand);
        getCommand("questlang").setTabCompleter(langCommand);
        getCommand("betonquestanswer").setTabCompleter((sender, command, label, args) -> List.of());
    }

    private void migrate() {
        try {
            new Migrator(loggerFactory).migrate();
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
        new LoadDataEvent(LoadDataEvent.State.PRE_LOAD).callEvent();
        questRegistry.loadData(getQuestPackageManager().getPackages().values());
        new LoadDataEvent(LoadDataEvent.State.POST_LOAD).callEvent();
        playerDataStorage.startObjectives();
        rpgMenu.syncCommands();
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
        defaultLanguage = config.getString("language", "en-US");
        questManager.reload();
        try {
            pluginMessage.reload();
        } catch (final IOException | QuestException e) {
            log.error("Could not reload the plugin messages!", e);
        }
        Notify.load(config, getQuestPackageManager().getPackages().values());
        lastExecutionCache.reload();

        // reload updater settings
        getUpdater().search();
        // stop current global locations listener
        // and start new one with reloaded configs
        log.debug("Restarting global locations");
        try {
            conversationColors.load();
        } catch (final QuestException e) {
            log.warn("Could not reload conversation colors! " + e.getMessage(), e);
        }
        compatibility.reload();
        // load all events, conditions, objectives, conversations etc.
        loadData();
        playerDataStorage.reloadProfiles(profileProvider.getOnlineProfiles());

        if (playerHider != null) {
            playerHider.stop();
        }
        try {
            playerHider = new PlayerHider(this, this, getVariableProcessor(), profileProvider, config);
        } catch (final QuestException e) {
            log.error("Could not start PlayerHider! " + e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public void onDisable() {
        if (questRegistry != null) {
            questRegistry.eventScheduling().clear();
        }
        // suspend all conversations
        if (profileProvider != null) {
            for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
                final Conversation conv = Conversation.getConversation(onlineProfile);
                if (conv != null) {
                    conv.suspend();
                }
                onlineProfile.getPlayer().closeInventory();
            }
        }
        // cancel database saver
        if (saver != null) {
            saver.end();
        }
        if (compatibility != null) {
            compatibility.disable();
        }
        if (database != null) {
            database.closeConnection();
        }
        if (playerHider != null) {
            playerHider.stop();
        }

        // done
        log.info("BetonQuest successfully disabled!");

        if (rpgMenu != null) {
            rpgMenu.onDisable();
        }
    }

    /**
     * Adds a Processor to re-/load data on BetonQuest re-/load.
     *
     * @param processor the processor to register
     */
    public void addProcessor(final QuestProcessor<?, ?> processor) {
        questRegistry.additional().add(processor);
    }

    @Override
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
    public ConfigAccessor getPluginConfig() {
        return config;
    }

    @Override
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Get the text parser.
     *
     * @return text parser
     */
    public TextParser getTextParser() {
        return textParser;
    }

    /**
     * Get the plugin messages provider.
     *
     * @return plugin messages provider
     */
    public PluginMessage getPluginMessage() {
        return pluginMessage;
    }

    @Override
    public ProfileProvider getProfileProvider() {
        return profileProvider;
    }

    @Override
    public QuestPackageManager getQuestPackageManager() {
        return questManager;
    }

    @Override
    public QuestTypeApi getQuestTypeApi() {
        return questRegistry.core();
    }

    @Override
    public FeatureApi getFeatureApi() {
        return questRegistry;
    }

    /**
     * Returns the database instance.
     *
     * @return Database instance
     */
    public Database getDB() {
        return database;
    }

    /**
     * Returns the updater instance.
     *
     * @return Updater instance
     */
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

    @Override
    public QuestTypeRegistries getQuestRegistries() {
        return questTypeRegistries;
    }

    @Override
    public FeatureRegistries getFeatureRegistries() {
        return featureRegistries;
    }

    @Override
    public PrimaryServerThreadData getPrimaryServerThreadData() {
        return new PrimaryServerThreadData(getServer(), getServer().getScheduler(), this);
    }

    /**
     * Get the VariableProcessor instance.
     *
     * @return the VariableProcessor to resolve variables
     */
    public VariableProcessor getVariableProcessor() {
        return questRegistry.core().variables();
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
        return fontRegistry;
    }
}
