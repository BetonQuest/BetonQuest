package org.betonquest.betonquest;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.LoadDataEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.ConfigurationFileFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.logger.CachingBetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.bstats.BStatsMetrics;
import org.betonquest.betonquest.commands.BackpackCommand;
import org.betonquest.betonquest.commands.CancelQuestCommand;
import org.betonquest.betonquest.commands.CompassCommand;
import org.betonquest.betonquest.commands.JournalCommand;
import org.betonquest.betonquest.commands.LangCommand;
import org.betonquest.betonquest.commands.QuestCommand;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.protocollib.FreezeEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.conversation.AnswerFilter;
import org.betonquest.betonquest.conversation.CombatTagger;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationResumer;
import org.betonquest.betonquest.conversation.Interceptor;
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.betonquest.betonquest.conversation.NonInterceptingInterceptor;
import org.betonquest.betonquest.conversation.SimpleConvIO;
import org.betonquest.betonquest.conversation.SimpleInterceptor;
import org.betonquest.betonquest.conversation.SlowTellrawConvIO;
import org.betonquest.betonquest.conversation.TellrawConvIO;
import org.betonquest.betonquest.database.AsyncSaver;
import org.betonquest.betonquest.database.Backup;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.menu.RPGMenu;
import org.betonquest.betonquest.modules.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.modules.config.DefaultConfigurationFileFactory;
import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.betonquest.betonquest.modules.logger.DefaultBetonQuestLoggerFactory;
import org.betonquest.betonquest.modules.logger.HandlerFactory;
import org.betonquest.betonquest.modules.logger.PlayerLogWatcher;
import org.betonquest.betonquest.modules.logger.handler.chat.AccumulatingReceiverSelector;
import org.betonquest.betonquest.modules.logger.handler.chat.ChatHandler;
import org.betonquest.betonquest.modules.logger.handler.history.HistoryHandler;
import org.betonquest.betonquest.modules.playerhider.PlayerHider;
import org.betonquest.betonquest.modules.schedule.EventScheduling;
import org.betonquest.betonquest.modules.schedule.LastExecutionCache;
import org.betonquest.betonquest.modules.schedule.impl.realtime.cron.RealtimeCronSchedule;
import org.betonquest.betonquest.modules.schedule.impl.realtime.cron.RealtimeCronScheduler;
import org.betonquest.betonquest.modules.schedule.impl.realtime.daily.RealtimeDailySchedule;
import org.betonquest.betonquest.modules.schedule.impl.realtime.daily.RealtimeDailyScheduler;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.java.JREVersionPrinter;
import org.betonquest.betonquest.modules.web.DownloadSource;
import org.betonquest.betonquest.modules.web.TempFileDownloadSource;
import org.betonquest.betonquest.modules.web.WebContentSource;
import org.betonquest.betonquest.modules.web.WebDownloadSource;
import org.betonquest.betonquest.modules.web.updater.UpdateDownloader;
import org.betonquest.betonquest.modules.web.updater.UpdateSourceHandler;
import org.betonquest.betonquest.modules.web.updater.Updater;
import org.betonquest.betonquest.modules.web.updater.UpdaterConfig;
import org.betonquest.betonquest.modules.web.updater.source.DevelopmentUpdateSource;
import org.betonquest.betonquest.modules.web.updater.source.ReleaseUpdateSource;
import org.betonquest.betonquest.modules.web.updater.source.implementations.GitHubReleaseSource;
import org.betonquest.betonquest.modules.web.updater.source.implementations.NexusReleaseAndDevelopmentSource;
import org.betonquest.betonquest.notify.ActionBarNotifyIO;
import org.betonquest.betonquest.notify.AdvancementNotifyIO;
import org.betonquest.betonquest.notify.BossBarNotifyIO;
import org.betonquest.betonquest.notify.ChatNotifyIO;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.SoundIO;
import org.betonquest.betonquest.notify.SubTitleNotifyIO;
import org.betonquest.betonquest.notify.SuppressNotifyIO;
import org.betonquest.betonquest.notify.TitleNotifyIO;
import org.betonquest.betonquest.notify.TotemNotifyIO;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.betonquest.betonquest.quest.registry.CoreQuestTypes;
import org.betonquest.betonquest.quest.registry.QuestRegistry;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.quest.registry.type.QuestTypeRegistry;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Event;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.InstantSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyMethods",
        "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.AtLeastOneConstructor", "PMD.ExcessivePublicCount", "PMD.TooManyFields", "NullAway.Init"})
public class BetonQuest extends JavaPlugin {
    private static final int BSTATS_METRICS_ID = 551;

    private static final Map<String, Class<? extends Objective>> OBJECTIVE_TYPES = new HashMap<>();

    private static final Map<String, Class<? extends ConversationIO>> CONVERSATION_IO_TYPES = new HashMap<>();

    private static final Map<String, Class<? extends Interceptor>> INTERCEPTOR_TYPES = new HashMap<>();

    private static final Map<String, Class<? extends NotifyIO>> NOTIFY_IO_TYPES = new HashMap<>();

    private static final Map<String, EventScheduling.ScheduleType<?>> SCHEDULE_TYPES = new HashMap<>();

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

    private final Map<Profile, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    /**
     * Stores Conditions, Events, Objectives, Variables, Conversations and Cancelers.
     */
    private QuestRegistry questRegistry;

    /**
     * Registry for quest core elements.
     */
    private QuestTypeRegistries questTypeRegistries;

    private BetonQuestLoggerFactory loggerFactory;

    private ConfigAccessorFactory configAccessorFactory;

    private ConfigurationFileFactory configurationFileFactory;

    private BetonQuestLogger log;

    private String pluginTag;

    private ConfigurationFile config;

    /**
     * The adventure instance.
     */
    private BukkitAudiences adventure;

    private Database database;

    private boolean isMySQLUsed;

    @SuppressWarnings("PMD.DoNotUseThreads")
    private AsyncSaver saver;

    private Updater updater;

    private GlobalData globalData;

    private PlayerHider playerHider;

    private RPGMenu rpgMenu;

    /**
     * Cache for event schedulers, holding the last execution of an event.
     */
    private LastExecutionCache lastExecutionCache;

    /**
     * Get the plugin's instance.
     *
     * @return The plugin's instance.
     */
    public static BetonQuest getInstance() {
        return instance;
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    public static boolean conditions(final Profile profile, final Collection<ConditionID> conditionIDs) {
        final ConditionID[] ids = new ConditionID[conditionIDs.size()];
        int index = 0;
        for (final ConditionID id : conditionIDs) {
            ids[index++] = id;
        }
        return conditions(profile, ids);
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    public static boolean conditions(@Nullable final Profile profile, final ConditionID... conditionIDs) {
        return instance.questRegistry.conditions().checks(profile, conditionIDs);
    }

    /**
     * Checks if the condition described by conditionID is met.
     *
     * @param conditionID ID of the condition to check
     * @param profile     the {@link Profile} of the player which should be checked
     * @return if the condition is met
     */
    public static boolean condition(@Nullable final Profile profile, final ConditionID conditionID) {
        return instance.questRegistry.conditions().check(profile, conditionID);
    }

    /**
     * Fires an event for the {@link Profile} if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} for which the event must be executed or null
     * @param eventID ID of the event to fire
     * @return true if the event was run even if there was an exception during execution
     */
    public static boolean event(@Nullable final Profile profile, final EventID eventID) {
        return instance.questRegistry.events().execute(profile, eventID);
    }

    /**
     * Creates new objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    public static void newObjective(final Profile profile, final ObjectiveID objectiveID) {
        instance.questRegistry.objectives().start(profile, objectiveID);
    }

    /**
     * Resumes the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    public static void resumeObjective(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        instance.questRegistry.objectives().resume(profile, objectiveID, instruction);
    }

    /**
     * Generates new instance of a Variable. If a similar one was already
     * created, it will return it instead of creating a new one.
     *
     * @param pack        package in which the variable is defined
     * @param instruction instruction of the variable, including both % characters.
     * @return the Variable instance
     * @throws InstructionParseException when the variable parsing fails
     */
    public static Variable createVariable(@Nullable final QuestPackage pack, final String instruction)
            throws InstructionParseException {
        return instance.questRegistry.variables().create(pack, instruction);
    }

    public static boolean isVariableType(final String type) {
        return instance.getQuestRegistries().getVariableTypes().getFactory(type) != null;
    }

    /**
     * @param name name of the notify IO type
     * @return the class object for this notify IO type
     */
    @Nullable
    public static Class<? extends NotifyIO> getNotifyIO(final String name) {
        return NOTIFY_IO_TYPES.get(name);
    }

    /**
     * Get the loaded Quest Canceller.
     *
     * @return quest cancellers in a new map
     */
    public static Map<QuestCancelerID, QuestCanceler> getCanceler() {
        return instance.questRegistry.questCanceller().getCancelers();
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

    public RPGMenu getRpgMenu() {
        return rpgMenu;
    }

    public ConfigurationFile getPluginConfig() {
        return config;
    }

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
            getServer().getScheduler().runTask(getInstance(), () -> getServer().getPluginManager().callEvent(event));
        }
    }

    private <T> T registerAndGetService(final Class<T> clazz, final T service) {
        final ServicesManager servicesManager = getServer().getServicesManager();
        servicesManager.register(clazz, service, this, ServicePriority.Lowest);
        return servicesManager.load(clazz);
    }

    @SuppressWarnings({"PMD.NcssCount", "PMD.DoNotUseThreads", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    public void onEnable() {
        instance = this;

        this.loggerFactory = registerAndGetService(BetonQuestLoggerFactory.class, new CachingBetonQuestLoggerFactory(new DefaultBetonQuestLoggerFactory()));
        this.configAccessorFactory = registerAndGetService(ConfigAccessorFactory.class, new DefaultConfigAccessorFactory());
        this.configurationFileFactory = registerAndGetService(ConfigurationFileFactory.class, new DefaultConfigurationFileFactory(loggerFactory, loggerFactory.create(DefaultConfigurationFileFactory.class), configAccessorFactory));

        this.log = loggerFactory.create(this);
        pluginTag = ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + getDescription().getName() + ChatColor.GRAY + "]" + ChatColor.RESET + " ";

        final JREVersionPrinter jreVersionPrinter = new JREVersionPrinter();
        final String jreInfo = jreVersionPrinter.getMessage();
        getInstance().log.info(jreInfo);

        migratePackages();

        try {
            config = configurationFileFactory.create(new File(getDataFolder(), "config.yml"), this, "config.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            getInstance().log.error("Could not load the config.yml file!", e);
            return;
        }

        final ConfigAccessor menuConfigAccessor;
        try {
            menuConfigAccessor = configAccessorFactory.create(new File(getDataFolder(), "menuConfig.yml"), this, "menuConfig.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            getInstance().log.error("Could not load the menuConfig.yml file!", e);
            return;
        }

        final HistoryHandler debugHistoryHandler = HandlerFactory.createHistoryHandler(this, this.getServer().getScheduler(), config, new File(getDataFolder(), "/logs"), InstantSource.system());
        registerLogHandler(getServer(), debugHistoryHandler);
        adventure = BukkitAudiences.create(this);
        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final ChatHandler chatHandler = HandlerFactory.createChatHandler(this, receiverSelector, adventure);
        registerLogHandler(getServer(), chatHandler);

        final String version = getDescription().getVersion();
        getInstance().log.debug("BetonQuest " + version + " is starting...");
        getInstance().log.debug(jreInfo);

        Config.setup(this, config);
        Notify.load();

        final boolean mySQLEnabled = config.getBoolean("mysql.enabled", true);
        if (mySQLEnabled) {
            getInstance().log.debug("Connecting to MySQL database");
            this.database = new MySQL(loggerFactory.create(MySQL.class, "Database"), this, config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.base"),
                    config.getString("mysql.user"),
                    config.getString("mysql.pass"));
            if (database.getConnection() != null) {
                isMySQLUsed = true;
                getInstance().log.info("Successfully connected to MySQL database!");
            }
        }
        if (!mySQLEnabled || !isMySQLUsed) {
            this.database = new SQLite(loggerFactory.create(SQLite.class, "Database"), this, "database.db");
            if (mySQLEnabled) {
                getInstance().log.warn("No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                getInstance().log.info("Using SQLite for storing data!");
            }
        }

        database.createTables();

        saver = new AsyncSaver(loggerFactory.create(AsyncSaver.class, "Database"));
        saver.start();
        Backup.loadDatabaseFromBackup(configAccessorFactory);

        new JoinQuitListener(loggerFactory);

        new QuestItemHandler();

        final ConfigAccessor cache;
        try {
            final Path cacheFile = new File(getDataFolder(), CACHE_FILE).toPath();
            if (!Files.exists(cacheFile)) {
                Files.createDirectories(Optional.ofNullable(cacheFile.getParent()).orElseThrow());
                Files.createFile(cacheFile);
            }
            cache = configAccessorFactory.create(cacheFile.toFile());
        } catch (final IOException | InvalidConfigurationException e) {
            this.log.error("Error while loading schedule cache: " + e.getMessage(), e);
            return;
        }
        lastExecutionCache = new LastExecutionCache(loggerFactory.create(LastExecutionCache.class, "Cache"), cache);

        new GlobalObjectives();

        new CombatTagger();

        ConversationColors.loadColors();

        new MobKillListener();

        new CustomDropListener(loggerFactory.create(CustomDropListener.class));

        new QuestCommand(loggerFactory, loggerFactory.create(QuestCommand.class), configAccessorFactory, adventure, new PlayerLogWatcher(receiverSelector), debugHistoryHandler);
        new JournalCommand();
        new BackpackCommand(loggerFactory.create(BackpackCommand.class));
        new CancelQuestCommand();
        new CompassCommand();
        new LangCommand(loggerFactory.create(LangCommand.class));

        questTypeRegistries = new QuestTypeRegistries(loggerFactory);

        questRegistry = new QuestRegistry(loggerFactory.create(QuestRegistry.class), loggerFactory, this,
                SCHEDULE_TYPES, questTypeRegistries, OBJECTIVE_TYPES);

        new CoreQuestTypes(loggerFactory, getServer(), getServer().getScheduler(), this).register(questTypeRegistries);

        registerConversationIO("simple", SimpleConvIO.class);
        registerConversationIO("tellraw", TellrawConvIO.class);
        registerConversationIO("chest", InventoryConvIO.class);
        registerConversationIO("combined", InventoryConvIO.Combined.class);
        registerConversationIO("slowtellraw", SlowTellrawConvIO.class);

        registerInterceptor("simple", SimpleInterceptor.class);
        registerInterceptor("none", NonInterceptingInterceptor.class);

        registerNotifyIO("suppress", SuppressNotifyIO.class);
        registerNotifyIO("chat", ChatNotifyIO.class);
        registerNotifyIO("advancement", AdvancementNotifyIO.class);
        registerNotifyIO("actionbar", ActionBarNotifyIO.class);
        registerNotifyIO("bossbar", BossBarNotifyIO.class);
        registerNotifyIO("title", TitleNotifyIO.class);
        registerNotifyIO("totem", TotemNotifyIO.class);
        registerNotifyIO("subtitle", SubTitleNotifyIO.class);
        registerNotifyIO("sound", SoundIO.class);

        registerScheduleType("realtime-daily", RealtimeDailySchedule.class, new RealtimeDailyScheduler(loggerFactory.create(RealtimeDailyScheduler.class, "Schedules"), lastExecutionCache));
        registerScheduleType("realtime-cron", RealtimeCronSchedule.class, new RealtimeCronScheduler(loggerFactory.create(RealtimeCronScheduler.class, "Schedules"), lastExecutionCache));

        new Compatibility();
        globalData = new GlobalData();

        // schedule quest data loading on the first tick, so all other
        // plugins can register their types
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Compatibility.postHook();
            loadData();
            for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                final PlayerData playerData = new PlayerData(onlineProfile);
                playerDataMap.put(onlineProfile, playerData);
                playerData.startObjectives();
                playerData.getJournal().update();
                if (playerData.getActiveConversation() != null) {
                    new ConversationResumer(loggerFactory, onlineProfile, playerData.getActiveConversation());
                }
            }

            try {
                playerHider = new PlayerHider();
            } catch (final InstructionParseException e) {
                getInstance().log.error("Could not start PlayerHider! " + e.getMessage(), e);
            }
        });

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
            getInstance().log.warn("Could not disable /betonquestanswer logging", e);
        }

        new BStatsMetrics(this, new Metrics(this, BSTATS_METRICS_ID), questRegistry.metricsSupplier());

        setupUpdater();

        rpgMenu = new RPGMenu(loggerFactory.create(RPGMenu.class), loggerFactory, menuConfigAccessor);

        PaperLib.suggestPaper(this);
        getInstance().log.info("BetonQuest successfully enabled!");
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

        final GitHubReleaseSource gitHubReleaseSource = new GitHubReleaseSource("https://api.github.com/repos/BetonQuest/BetonQuest",
                new WebContentSource(GitHubReleaseSource.HTTP_CODE_HANDLER));
        final NexusReleaseAndDevelopmentSource nexusReleaseAndDevelopmentSource = new NexusReleaseAndDevelopmentSource("https://nexus.betonquest.org/",
                new WebContentSource());
        final List<ReleaseUpdateSource> releaseHandlers = List.of(gitHubReleaseSource, nexusReleaseAndDevelopmentSource);
        final List<DevelopmentUpdateSource> developmentHandlers = List.of(nexusReleaseAndDevelopmentSource);
        final UpdateSourceHandler updateSourceHandler = new UpdateSourceHandler(loggerFactory.create(UpdateSourceHandler.class), releaseHandlers, developmentHandlers);

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
        instance.questRegistry.loadData(Config.getPackages().values());

        // start those freshly loaded objectives for all players
        for (final PlayerData playerData : playerDataMap.values()) {
            playerData.startObjectives();
        }

        rpgMenu.reloadData();

        Bukkit.getPluginManager().callEvent(new LoadDataEvent());
    }

    /**
     * Reloads the plugin.
     */
    public void reload() {
        // reload the configuration
        getInstance().log.debug("Reloading configuration");
        try {
            config.reload();
        } catch (final IOException e) {
            getInstance().log.warn("Could not reload config! " + e.getMessage(), e);
        }
        Config.setup(this, config);
        Notify.load();
        lastExecutionCache.reload();

        // reload updater settings
        getInstance().getUpdater().search();
        // stop current global locations listener
        // and start new one with reloaded configs
        getInstance().log.debug("Restarting global locations");
        new GlobalObjectives();
        ConversationColors.loadColors();
        Compatibility.reload();
        // load all events, conditions, objectives, conversations etc.
        loadData();
        // start objectives and update journals for every online profiles
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            getInstance().log.debug("Updating journal for player " + onlineProfile);
            final PlayerData playerData = instance.getPlayerData(onlineProfile);
            GlobalObjectives.startAll(onlineProfile);
            final Journal journal = playerData.getJournal();
            journal.update();
        }
        if (playerHider != null) {
            playerHider.stop();
        }
        try {
            playerHider = new PlayerHider();
        } catch (final InstructionParseException e) {
            getInstance().log.error("Could not start PlayerHider! " + e.getMessage(), e);
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
        getInstance().log.info("BetonQuest succesfully disabled!");

        if (this.adventure != null) {
            this.adventure.close();
        }

        if (rpgMenu != null) {
            rpgMenu.onDisable();
        }
        FreezeEvent.cleanup();
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
     * Returns the schedules cache instance.
     *
     * @return LastExecutionCache instance
     */
    public LastExecutionCache getLastExecutionCache() {
        return lastExecutionCache;
    }

    /**
     * Checks if MySQL is used or not.
     *
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return isMySQLUsed;
    }

    /**
     * Stores the PlayerData in a map, so it can be retrieved using
     * getPlayerData(Profile profile).
     *
     * @param profile    the {@link Profile} of the player
     * @param playerData PlayerData object to store
     */
    public void putPlayerData(final Profile profile, final PlayerData playerData) {
        getInstance().log.debug("Inserting data for " + profile);
        playerDataMap.put(profile, playerData);
    }

    /**
     * Retrieves PlayerData object for specified profile. If the playerData does
     * not exist it will create new playerData on the main thread and put it
     * into the map.
     *
     * @param profile the {@link OnlineProfile} of the player
     * @return PlayerData object for the player
     */
    public PlayerData getPlayerData(final OnlineProfile profile) {
        return getPlayerData((Profile) profile);
    }

    /**
     * Retrieves PlayerData object for specified profile. If the playerData does
     * not exist but the profile is online, it will create new playerData on the
     * main thread and put it into the map.
     *
     * @param profile the {@link Profile} of the player
     * @return PlayerData object for the player
     * @throws IllegalArgumentException when there is no data and the player is offline
     */
    public PlayerData getPlayerData(final Profile profile) {
        PlayerData playerData = playerDataMap.get(profile);
        if (playerData == null) {
            if (profile.getOnlineProfile().isPresent()) {
                playerData = new PlayerData(profile);
                putPlayerData(profile, playerData);
            } else {
                throw new IllegalArgumentException("The profile has no online player!");
            }
        }
        return playerData;
    }

    public PlayerData getOfflinePlayerData(final Profile profile) {
        if (profile.getOnlineProfile().isPresent()) {
            return getPlayerData(profile);
        }
        return new PlayerData(profile);
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
     * Removes the database playerData from the map.
     *
     * @param profile the {@link Profile} of the player whose playerData is to be removed
     */
    public void removePlayerData(final Profile profile) {
        playerDataMap.remove(profile);
    }

    /**
     * Registers new condition classes by their names.
     *
     * @param name           name of the condition type
     * @param conditionClass class object for the condition
     * @deprecated replaced by {@link #getQuestRegistries()}
     * further {@link QuestTypeRegistries#getConditionTypes()}
     * further {@linkplain QuestTypeRegistry#registerCombined}
     */
    @Deprecated
    public void registerConditions(final String name, final Class<? extends Condition> conditionClass) {
        questTypeRegistries.getConditionTypes().register(name, conditionClass);
    }

    /**
     * Registers an event with its name and the class used to create instances of the event.
     *
     * @param name       name of the event type
     * @param eventClass class object for the event
     * @deprecated replaced by {@link #registerEvent(String, EventFactory, StaticEventFactory)}
     */
    @Deprecated
    public void registerEvents(final String name, final Class<? extends QuestEvent> eventClass) {
        questTypeRegistries.getEventTypes().register(name, eventClass);
    }

    /**
     * Registers an event that does not support static execution with its name
     * and a factory to create new normal instances of the event.
     *
     * @param name         name of the event
     * @param eventFactory factory to create the event
     * @deprecated in favor of direct usage of {@link #getQuestRegistries()}
     * further {@link QuestTypeRegistries#getEventTypes()}
     */
    @Deprecated
    public void registerNonStaticEvent(final String name, final EventFactory eventFactory) {
        questTypeRegistries.getEventTypes().register(name, eventFactory);
    }

    /**
     * Registers an event with its name and a single factory to create both normal and
     * static instances of the event.
     *
     * @param name         name of the event
     * @param eventFactory factory to create the event and the static event
     * @param <T>          type of factory that creates both normal and static instances of the event.
     * @deprecated in favor of direct usage of {@link #getQuestRegistries()}
     * further {@link QuestTypeRegistries#getEventTypes()}
     * further {@link QuestTypeRegistry#registerCombined(String, PlayerQuestFactory)}
     */
    @Deprecated
    public <T extends EventFactory & StaticEventFactory> void registerEvent(final String name, final T eventFactory) {
        questTypeRegistries.getEventTypes().registerCombined(name, eventFactory);
    }

    /**
     * Registers an event with its name and two factories to create normal and
     * static instances of the event.
     *
     * @param name               name of the event
     * @param eventFactory       factory to create the event
     * @param staticEventFactory factory to create the static event
     * @deprecated in favor of direct usage of {@link #getQuestRegistries()}
     * further {@link QuestTypeRegistries#getEventTypes()}
     * further {@link QuestTypeRegistry#register(String, PlayerQuestFactory, PlayerlessQuestFactory)}
     */
    @Deprecated
    public void registerEvent(final String name, final EventFactory eventFactory, final StaticEventFactory staticEventFactory) {
        questTypeRegistries.getEventTypes().register(name, eventFactory, staticEventFactory);
    }

    /**
     * Registers new objective classes by their names.
     *
     * @param name           name of the objective type
     * @param objectiveClass class object for the objective
     */
    public void registerObjectives(final String name, final Class<? extends Objective> objectiveClass) {
        getInstance().log.debug("Registering " + name + " objective type");
        OBJECTIVE_TYPES.put(name, objectiveClass);
    }

    /**
     * Registers new conversation input/output class.
     *
     * @param name        name of the IO type
     * @param convIOClass class object to register
     */
    public void registerConversationIO(final String name, final Class<? extends ConversationIO> convIOClass) {
        getInstance().log.debug("Registering " + name + " conversation IO type");
        CONVERSATION_IO_TYPES.put(name, convIOClass);
    }

    /**
     * Registers new interceptor class.
     *
     * @param name             name of the interceptor type
     * @param interceptorClass class object to register
     */
    public void registerInterceptor(final String name, final Class<? extends Interceptor> interceptorClass) {
        getInstance().log.debug("Registering " + name + " interceptor type");
        INTERCEPTOR_TYPES.put(name, interceptorClass);
    }

    /**
     * Registers new notify input/output class.
     *
     * @param name    name of the IO type
     * @param ioClass class object to register
     */
    public void registerNotifyIO(final String name, final Class<? extends NotifyIO> ioClass) {
        getInstance().log.debug("Registering " + name + " notify IO type");
        NOTIFY_IO_TYPES.put(name, ioClass);
    }

    /**
     * Registers new variable type.
     *
     * @param name     name of the variable type
     * @param variable class object of this type
     * @deprecated in favor of direct usage of {@link #getQuestRegistries()}
     * further {@link QuestTypeRegistries#getVariableTypes()}
     */
    @Deprecated
    public void registerVariable(final String name, final Class<? extends Variable> variable) {
        getQuestRegistries().getVariableTypes().register(name, variable);
    }

    /**
     * Register a new schedule type.
     *
     * @param name      name of the schedule type
     * @param schedule  class object of the schedule type
     * @param scheduler instance of the scheduler
     * @param <S>       type of schedule
     */
    public <S extends Schedule> void registerScheduleType(final String name, final Class<S> schedule, final Scheduler<S> scheduler) {
        SCHEDULE_TYPES.put(name, new EventScheduling.ScheduleType<>(schedule, scheduler));
    }

    /**
     * Returns the list of objectives of this player.
     *
     * @param profile the {@link Profile} of the player
     * @return list of this player's active objectives
     */
    public List<Objective> getPlayerObjectives(final Profile profile) {
        return questRegistry.objectives().getActive(profile);
    }

    /**
     * Gets stored Conversation Data.
     * <p>
     * The conversation data can be null if there was an error loading it.
     *
     * @param conversationID package name, dot and name of the conversation
     * @return ConversationData object for this conversation or null if it does
     * not exist
     */
    @Nullable
    public ConversationData getConversation(final ConversationID conversationID) {
        return instance.questRegistry.conversations().getConversation(conversationID);
    }

    /**
     * @param objectiveID package name, dot and ID of the objective
     * @return Objective object or null if it does not exist
     */
    @Nullable
    public Objective getObjective(final ObjectiveID objectiveID) {
        return instance.questRegistry.objectives().getObjective(objectiveID);
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
     * @param name name of the conversation IO type
     * @return the class object for this conversation IO type
     */
    @Nullable
    public Class<? extends ConversationIO> getConvIO(final String name) {
        return CONVERSATION_IO_TYPES.get(name);
    }

    /**
     * @param name name of the interceptor type
     * @return the class object for this interceptor type
     */
    @Nullable
    public Class<? extends Interceptor> getInterceptor(final String name) {
        return INTERCEPTOR_TYPES.get(name);
    }

    /**
     * Resoles the variable for specified player. If the variable is not loaded
     * it will load it on the main thread.
     *
     * @param packName name of the package
     * @param name     name of the variable (instruction, with % characters)
     * @param profile  the {@link Profile} of the player
     * @return the value of this variable for given player
     * @deprecated use {@link #getVariableProcessor()} {@link VariableProcessor#getValue(QuestPackage, String, Profile)}
     * instead
     */
    @Deprecated
    public String getVariableValue(final String packName, final String name, @Nullable final Profile profile) {
        final QuestPackage pack = Config.getPackages().get(packName);
        if (pack == null) {
            log.warn("The variable '" + name + "' reference the non-existent package '" + packName + "' !");
            return "";
        }
        try {
            return questRegistry.variables().getValue(pack, name, profile);
        } catch (final InstructionParseException e) {
            log.warn(e.getMessage(), e);
            return "";
        }
    }

    /**
     * Fetches the factory to create the event registered with the given name.
     *
     * @param name the name of the event
     * @return a factory to create the event
     * @deprecated in favor of direct usage of {@link #getQuestRegistries()}
     * further {@link QuestTypeRegistries#getEventTypes()}
     * further {@link QuestTypeRegistry#getFactory(String)}
     */
    @Deprecated
    @Nullable
    public LegacyTypeFactory<QuestEvent> getEventFactory(final String name) {
        return questTypeRegistries.getEventTypes().getFactory(name);
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
     * Renames the objective instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        questRegistry.objectives().renameObjective(name, rename);
    }

    /**
     * @return the objective types map
     */
    public Map<String, Class<? extends Objective>> getObjectiveTypes() {
        return new HashMap<>(OBJECTIVE_TYPES);
    }

    /**
     * Get the VariableProcessor instance.
     *
     * @return the VariableProcessor to resolve variables
     */
    public VariableProcessor getVariableProcessor() {
        return questRegistry.variables();
    }
}
