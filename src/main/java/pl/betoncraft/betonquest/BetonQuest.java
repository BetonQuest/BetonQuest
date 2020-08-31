/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.betoncraft.betonquest.api.*;
import pl.betoncraft.betonquest.commands.*;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.conditions.*;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.config.ConfigUpdater;
import pl.betoncraft.betonquest.conversation.*;
import pl.betoncraft.betonquest.database.*;
import pl.betoncraft.betonquest.events.*;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.id.VariableID;
import pl.betoncraft.betonquest.item.QuestItemHandler;
import pl.betoncraft.betonquest.notify.*;
import pl.betoncraft.betonquest.objectives.*;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Updater;
import pl.betoncraft.betonquest.utils.Utils;
import pl.betoncraft.betonquest.variables.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents BetonQuest plugin
 *
 * @author Jakub Sapalski
 */
public class BetonQuest extends JavaPlugin {

    private static BetonQuest instance;
    private static HashMap<String, Class<? extends Condition>> conditionTypes = new HashMap<>();
    private static HashMap<String, Class<? extends QuestEvent>> eventTypes = new HashMap<>();
    private static HashMap<String, Class<? extends Objective>> objectiveTypes = new HashMap<>();
    private static HashMap<String, Class<? extends ConversationIO>> convIOTypes = new HashMap<>();
    private static HashMap<String, Class<? extends Interceptor>> interceptorTypes = new HashMap<>();
    private static HashMap<String, Class<? extends NotifyIO>> notifyIOTypes = new HashMap<>();
    private static HashMap<String, Class<? extends Variable>> variableTypes = new HashMap<>();
    private static HashMap<ConditionID, Condition> conditions = new HashMap<>();
    private static HashMap<EventID, QuestEvent> events = new HashMap<>();
    private static HashMap<ObjectiveID, Objective> objectives = new HashMap<>();
    private static HashMap<String, ConversationData> conversations = new HashMap<>();
    private static HashMap<VariableID, Variable> variables = new HashMap<>();
    private Database database;
    private boolean isMySQLUsed;
    private Saver saver;
    private Updater updater;
    private final ConcurrentHashMap<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private GlobalData globalData;

    public BetonQuest() {
        super();
        instance = this;
    }

    /**
     * Returns the plugin's instance
     *
     * @return the plugin's instance
     */
    public static BetonQuest getInstance() {
        return instance;
    }

    /**
     * Checks if the condition described by conditionID is met
     *
     * @param conditionID ID of the condition to check, as defined in conditions.yml
     * @param playerID    ID of the player which should be checked
     * @return if the condition is met
     */
    public static boolean condition(final String playerID, final ConditionID conditionID) {
        // null check
        if (conditionID == null) {
            LogUtils.getLogger().log(Level.FINE, "Null condition ID!");
            return false;
        }
        // get the condition
        Condition condition = null;
        for (final Entry<ConditionID, Condition> e : conditions.entrySet()) {
            if (e.getKey().equals(conditionID)) {
                condition = e.getValue();
                break;
            }
        }
        if (condition == null) {
            LogUtils.getLogger().log(Level.WARNING, "The condition " + conditionID + " is not defined!");
            return false;
        }
        // check for null player
        if (playerID == null && !condition.isStatic()) {
            LogUtils.getLogger().log(Level.FINE, "Cannot check non-static condition without a player, returning false");
            return false;
        }
        // check for online player
        if (playerID != null && PlayerConverter.getPlayer(playerID) == null && !condition.isPersistent()) {
            LogUtils.getLogger().log(Level.FINE, "Player was offline, condition is not persistent, returning false");
            return false;
        }
        // and check if it's met or not
        boolean outcome = false;
        try {
            outcome = condition.handle(playerID);
        } catch (final QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING,
                    "Error while checking '" + conditionID + "' condition: " + e.getMessage());
            LogUtils.logThrowable(e);
            return false;
        }
        final boolean isMet = outcome != conditionID.inverted();
        LogUtils.getLogger().log(Level.FINE,
                (isMet ? "TRUE" : "FALSE") + ": " + (conditionID.inverted() ? "inverted" : "") + " condition "
                        + conditionID + " for player " + PlayerConverter.getName(playerID));
        return isMet;
    }

    /**
     * Fires the event described by eventID
     *
     * @param eventID  ID of the event to fire, as defined in events.yml
     * @param playerID ID of the player who the event is firing for
     */
    public static void event(final String playerID, final EventID eventID) {
        // null check
        if (eventID == null) {
            LogUtils.getLogger().log(Level.FINE, "Null event ID!");
            return;
        }
        // get the event
        QuestEvent event = null;
        for (final Entry<EventID, QuestEvent> e : events.entrySet()) {
            if (e.getKey().equals(eventID)) {
                event = e.getValue();
                break;
            }
        }
        if (event == null) {
            LogUtils.getLogger().log(Level.WARNING, "Event " + eventID + " is not defined");
            return;
        }
        // fire the event
        if (playerID == null) {
            LogUtils.getLogger().log(Level.FINE, "Firing static event " + eventID);
        } else {
            LogUtils.getLogger().log(Level.FINE,
                    "Firing event " + eventID + " for " + PlayerConverter.getName(playerID));
        }
        try {
            event.fire(playerID);
        } catch (final QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while firing '" + eventID + "' event: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
    }

    /**
     * Creates new objective for given player
     *
     * @param playerID    ID of the player
     * @param objectiveID ID of the objective
     */
    public static void newObjective(final String playerID, final ObjectiveID objectiveID) {
        // null check
        if (playerID == null || objectiveID == null) {
            LogUtils.getLogger().log(Level.FINE, "Null arguments for the objective!");
            return;
        }
        Objective objective = null;
        for (final Entry<ObjectiveID, Objective> e : objectives.entrySet()) {
            if (e.getKey().equals(objectiveID)) {
                objective = e.getValue();
                break;
            }
        }
        if (objective.containsPlayer(playerID)) {
            LogUtils.getLogger().log(Level.FINE,
                    "Player " + PlayerConverter.getName(playerID) + " already has the " + objectiveID +
                            " objective");
            return;
        }
        objective.newPlayer(playerID);
    }

    /**
     * Resumes the existing objective for given player
     *
     * @param playerID    ID of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    public static void resumeObjective(final String playerID, final ObjectiveID objectiveID, final String instruction) {
        // null check
        if (playerID == null || objectiveID == null || instruction == null) {
            LogUtils.getLogger().log(Level.FINE, "Null arguments for the objective!");
            return;
        }
        Objective objective = null;
        for (final Entry<ObjectiveID, Objective> e : objectives.entrySet()) {
            if (e.getKey().equals(objectiveID)) {
                objective = e.getValue();
                break;
            }
        }
        if (objective == null) {
            LogUtils.getLogger().log(Level.WARNING, "Objective " + objectiveID + " does not exist");
            return;
        }
        if (objective.containsPlayer(playerID)) {
            LogUtils.getLogger().log(Level.FINE,
                    "Player " + PlayerConverter.getName(playerID) + " already has the " + objectiveID + " objective!");
            return;
        }
        objective.addPlayer(playerID, instruction);
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
    public static Variable createVariable(final ConfigPackage pack, final String instruction)
            throws InstructionParseException {
        final VariableID variableID;
        try {
            variableID = new VariableID(pack, instruction);
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException("Could not load variable: " + e.getMessage(), e);
        }
        // no need to create duplicated variables
        for (final Entry<VariableID, Variable> e : variables.entrySet()) {
            if (e.getKey().equals(variableID)) {
                return e.getValue();
            }
        }
        final String[] parts = instruction.replace("%", "").split("\\.");
        if (parts.length < 1) {
            throw new InstructionParseException("Not enough arguments in variable " + variableID);
        }
        final Class<? extends Variable> variableClass = variableTypes.get(parts[0]);
        // if it's null then there is no such type registered, log an error
        if (variableClass == null) {
            throw new InstructionParseException("Variable type " + parts[0] + " is not registered");
        }
        try {
            final Variable variable = variableClass.getConstructor(Instruction.class)
                    .newInstance(new VariableInstruction(pack, null, instruction));
            variables.put(variableID, variable);
            LogUtils.getLogger().log(Level.FINE, "Variable " + variableID + " loaded");
            return variable;
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in " + variableID + " variable: " + e.getCause().getMessage(), e);
            } else {
                LogUtils.logThrowableReport(e);
            }
        } catch (final Exception e) {
            LogUtils.logThrowableReport(e);
        }
        return null;
    }

    /**
     * Resolves variables in the supplied text and returns them as a list of
     * instruction strings, including % characters. Variables are unique, so if
     * the user uses the same variables multiple times, the list will contain
     * only one occurence of this variable.
     *
     * @param text text from which the variables will be resolved
     * @return the list of unique variable instructions
     */
    public static ArrayList<String> resolveVariables(final String text) {
        final ArrayList<String> variables = new ArrayList<>();
        final Matcher matcher = Pattern.compile("%[^ %\\s]+%").matcher(text);
        while (matcher.find()) {
            final String variable = matcher.group();
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
        return variables;
    }

    /**
     * @param name name of the notify IO type
     * @return the class object for this notify IO type
     */
    public static Class<? extends NotifyIO> getNotifyIO(final String name) {
        return notifyIOTypes.get(name);
    }

    @Override
    public void onEnable() {

        // initialize debugger
        LogUtils.setupLogger();

        // load configuration
        new Config();

        // try to connect to database
        LogUtils.getLogger().log(Level.FINE, "Connecting to MySQL database");
        Connection con = null;
        if (getConfig().getBoolean("mysql.enabled", true)) {
            this.database = new MySQL(this, getConfig().getString("mysql.host"),
                    getConfig().getString("mysql.port"),
                    getConfig().getString("mysql.base"), getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.pass"));
            con = database.getConnection();
        }
        if (con == null) {
            this.database = new SQLite(this, "database.db");
            LogUtils.getLogger().log(Level.INFO, "Using SQLite for storing data!");
            isMySQLUsed = false;
        } else {
            LogUtils.getLogger().log(Level.INFO, "Using MySQL for storing data!");
            isMySQLUsed = true;
        }

        // create tables in the database
        database.createTables(isMySQLUsed);

        // create and start the saver object, which handles correct asynchronous
        // saving to the database
        saver = new Saver();
        saver.start();

        // load database backup
        Utils.loadDatabaseFromBackup();

        // update configuration if needed
        new ConfigUpdater();

        // if it's a first start of the plugin, debug option is not there
        // add it so debug option is turned off after first start
        if (getConfig().getString("debug", null) == null) {
            getConfig().set("debug", "false");
            saveConfig();
        }

        // instantiating of these important things
        new JoinQuitListener();

        // instantiate journal handler
        new QuestItemHandler();

        // initialize static events
        new StaticEvents();

        // initialize global objectives
        new GlobalObjectives();

        // initialize combat tagging
        new CombatTagger();

        // load colors for conversations
        new ConversationColors();

        // start mob kill listener
        new MobKillListener();

        // start custom drop listener
        new CustomDropListener();

        // register commands
        new QuestCommand();
        new JournalCommand();
        new BackpackCommand();
        new CancelQuestCommand();
        new CompassCommand();
        new LangCommand();

        // register conditions
        registerConditions("health", HealthCondition.class);
        registerConditions("permission", PermissionCondition.class);
        registerConditions("experience", ExperienceCondition.class);
        registerConditions("tag", TagCondition.class);
        registerConditions("globaltag", GlobalTagCondition.class);
        registerConditions("point", PointCondition.class);
        registerConditions("globalpoint", GlobalPointCondition.class);
        registerConditions("and", ConjunctionCondition.class);
        registerConditions("or", AlternativeCondition.class);
        registerConditions("time", TimeCondition.class);
        registerConditions("weather", WeatherCondition.class);
        registerConditions("height", HeightCondition.class);
        registerConditions("item", ItemCondition.class);
        registerConditions("hand", HandCondition.class);
        registerConditions("location", LocationCondition.class);
        registerConditions("armor", ArmorCondition.class);
        registerConditions("effect", EffectCondition.class);
        registerConditions("rating", ArmorRatingCondition.class);
        registerConditions("sneak", SneakCondition.class);
        registerConditions("random", RandomCondition.class);
        registerConditions("journal", JournalCondition.class);
        registerConditions("testforblock", TestForBlockCondition.class);
        registerConditions("empty", EmptySlotsCondition.class);
        registerConditions("party", PartyCondition.class);
        registerConditions("entities", EntityCondition.class);
        registerConditions("objective", ObjectiveCondition.class);
        registerConditions("check", CheckCondition.class);
        registerConditions("chestitem", ChestItemCondition.class);
        registerConditions("score", ScoreboardCondition.class);
        registerConditions("riding", VehicleCondition.class);
        registerConditions("world", WorldCondition.class);
        registerConditions("gamemode", GameModeCondition.class);
        registerConditions("advancement", AdvancementCondition.class);
        registerConditions("variable", VariableCondition.class);
        registerConditions("biome", BiomeCondition.class);
        registerConditions("dayofweek", DayOfWeekCondition.class);
        registerConditions("partialdate", PartialDateCondition.class);
        registerConditions("realtime", RealTimeCondition.class);
        registerConditions("looking", LookingAtCondition.class);
        registerConditions("facing", FacingCondition.class);
        registerConditions("conversation", ConversationCondition.class);
        registerConditions("mooncycle", MooncycleCondition.class);
        registerConditions("fly", FlyingCondition.class);

        // register events
        registerEvents("message", MessageEvent.class);
        registerEvents("objective", ObjectiveEvent.class);
        registerEvents("command", CommandEvent.class);
        registerEvents("tag", TagEvent.class);
        registerEvents("globaltag", GlobalTagEvent.class);
        registerEvents("journal", JournalEvent.class);
        registerEvents("teleport", TeleportEvent.class);
        registerEvents("explosion", ExplosionEvent.class);
        registerEvents("lightning", LightningEvent.class);
        registerEvents("point", PointEvent.class);
        registerEvents("globalpoint", GlobalPointEvent.class);
        registerEvents("give", GiveEvent.class);
        registerEvents("take", TakeEvent.class);
        registerEvents("conversation", ConversationEvent.class);
        registerEvents("kill", KillEvent.class);
        registerEvents("effect", EffectEvent.class);
        registerEvents("deleffect", DelEffectEvent.class);
        registerEvents("deletepoint", DeletePointEvent.class);
        registerEvents("spawn", SpawnMobEvent.class);
        registerEvents("killmob", KillMobEvent.class);
        registerEvents("time", TimeEvent.class);
        registerEvents("weather", WeatherEvent.class);
        registerEvents("folder", FolderEvent.class);
        registerEvents("setblock", SetBlockEvent.class);
        registerEvents("damage", DamageEvent.class);
        registerEvents("party", PartyEvent.class);
        registerEvents("clear", ClearEvent.class);
        registerEvents("run", RunEvent.class);
        registerEvents("givejournal", GiveJournalEvent.class);
        registerEvents("sudo", SudoEvent.class);
        registerEvents("opsudo", OpSudoEvent.class);
        registerEvents("chestgive", ChestGiveEvent.class);
        registerEvents("chesttake", ChestTakeEvent.class);
        registerEvents("chestclear", ChestClearEvent.class);
        registerEvents("compass", CompassEvent.class);
        registerEvents("cancel", CancelEvent.class);
        registerEvents("score", ScoreboardEvent.class);
        registerEvents("lever", LeverEvent.class);
        registerEvents("door", DoorEvent.class);
        registerEvents("if", IfElseEvent.class);
        registerEvents("variable", VariableEvent.class);
        registerEvents("title", TitleEvent.class);
        registerEvents("language", LanguageEvent.class);
        registerEvents("playsound", PlaysoundEvent.class);
        registerEvents("pickrandom", PickRandomEvent.class);
        registerEvents("experience", ExperienceEvent.class);
        registerEvents("notify", NotifyEvent.class);
        registerEvents("chat", ChatEvent.class);

        // register objectives
        registerObjectives("location", LocationObjective.class);
        registerObjectives("block", BlockObjective.class);
        registerObjectives("mobkill", MobKillObjective.class);
        registerObjectives("action", ActionObjective.class);
        registerObjectives("die", DieObjective.class);
        registerObjectives("craft", CraftingObjective.class);
        registerObjectives("smelt", SmeltingObjective.class);
        registerObjectives("tame", TameObjective.class);
        registerObjectives("delay", DelayObjective.class);
        registerObjectives("arrow", ArrowShootObjective.class);
        registerObjectives("experience", ExperienceObjective.class);
        registerObjectives("step", StepObjective.class);
        registerObjectives("logout", LogoutObjective.class);
        registerObjectives("password", PasswordObjective.class);
        registerObjectives("pickup", PickupObjective.class);
        registerObjectives("fish", FishObjective.class);
        registerObjectives("enchant", EnchantObjective.class);
        registerObjectives("shear", ShearObjective.class);
        registerObjectives("chestput", ChestPutObjective.class);
        registerObjectives("brew", BrewObjective.class);
        registerObjectives("vehicle", VehicleObjective.class);
        registerObjectives("consume", ConsumeObjective.class);
        registerObjectives("variable", VariableObjective.class);
        registerObjectives("kill", KillPlayerObjective.class);
        registerObjectives("interact", EntityInteractObjective.class);
        registerObjectives("respawn", RespawnObjective.class);
        registerObjectives("breed", BreedObjective.class);

        // register conversation IO types
        registerConversationIO("simple", SimpleConvIO.class);
        registerConversationIO("tellraw", TellrawConvIO.class);
        registerConversationIO("chest", InventoryConvIO.class);
        registerConversationIO("combined", InventoryConvIO.Combined.class);
        registerConversationIO("slowtellraw", SlowTellrawConvIO.class);

        // register interceptor types
        registerInterceptor("simple", SimpleInterceptor.class);
        registerInterceptor("none", NonInterceptingInterceptor.class);

        // register notify IO types
        registerNotifyIO("suppress", SuppressNotifyIO.class);
        registerNotifyIO("chat", ChatNotifyIO.class);
        registerNotifyIO("advancement", AdvancementNotifyIO.class);
        registerNotifyIO("actionbar", ActionBarNotifyIO.class);
        registerNotifyIO("bossbar", BossBarNotifyIO.class);
        registerNotifyIO("title", TitleNotifyIO.class);
        registerNotifyIO("subtitle", SubTitleNotifyIO.class);

        // register variable types
        registerVariable("player", PlayerNameVariable.class);
        registerVariable("npc", NpcNameVariable.class);
        registerVariable("objective", ObjectivePropertyVariable.class);
        registerVariable("point", PointVariable.class);
        registerVariable("globalpoint", GlobalPointVariable.class);
        registerVariable("item", ItemAmountVariable.class);
        registerVariable("version", VersionVariable.class);
        registerVariable("location", LocationVariable.class);
        registerVariable("math", MathVariable.class);

        // initialize compatibility with other plugins
        new Compatibility();

        // schedule quest data loading on the first tick, so all other
        // plugins can register their types
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            @Override
            public void run() {
                // Load all events and conditions
                loadData();
                // Load global tags and points
                globalData = new GlobalData();
                // load data for all online players
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final String playerID = PlayerConverter.getID(player);
                    final PlayerData playerData = new PlayerData(playerID);
                    playerDataMap.put(playerID, playerData);
                    playerData.startObjectives();
                    playerData.getJournal().update();
                    if (playerData.getConversation() != null) {
                        new ConversationResumer(playerID, playerData.getConversation());
                    }
                }
            }
        });

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LogUtils.getLogger().log(Level.WARNING, "Could not disable /betonquestanswer logging");
            LogUtils.logThrowable(e);
        }

        // metrics
        new Metrics(this, 551);

        // updater
        updater = new Updater(this.getFile());

        // done
        LogUtils.getLogger().log(Level.INFO, "BetonQuest succesfully enabled!");
    }

    /**
     * Loads events and conditions to the maps
     */
    public void loadData() {
        // save data of all objectives to the players
        for (final Objective objective : objectives.values()) {
            objective.close();
        }
        // clear previously loaded data
        events.clear();
        conditions.clear();
        conversations.clear();
        objectives.clear();
        variables.clear();
        // load new data
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            LogUtils.getLogger().log(Level.FINE, "Loading stuff in package " + packName);
            final FileConfiguration eConfig = Config.getPackages().get(packName).getEvents().getConfig();
            for (final String key : eConfig.getKeys(false)) {
                if (key.contains(" ")) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Event name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final EventID identifier;
                try {
                    identifier = new EventID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Error while loading event '" + packName + "." + key + "': " + e.getMessage());
                    LogUtils.logThrowable(e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.generateInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Objective type not defined in '" + packName + "." + key + "'");
                    LogUtils.logThrowable(e);
                    continue;
                }
                final Class<? extends QuestEvent> eventClass = eventTypes.get(type);
                if (eventClass == null) {
                    // if it's null then there is no such type registered, log
                    // an error
                    LogUtils.getLogger().log(Level.WARNING, "Event type " + type + " is not registered, check if it's"
                            + " spelled correctly in '" + identifier + "' event.");
                    continue;
                }
                try {
                    final QuestEvent event = eventClass.getConstructor(Instruction.class)
                            .newInstance(identifier.generateInstruction());
                    events.put(identifier, event);
                    LogUtils.getLogger().log(Level.FINE, "  Event '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LogUtils.getLogger().log(Level.WARNING,
                                "Error in '" + identifier + "' event (" + type + "): " + e.getCause().getMessage());
                        LogUtils.logThrowable(e);
                    } else {
                        LogUtils.logThrowableReport(e);
                    }
                } catch (final Exception e) {
                    LogUtils.logThrowableReport(e);
                }
            }
            final FileConfiguration cConfig = pack.getConditions().getConfig();
            for (final String key : cConfig.getKeys(false)) {
                if (key.contains(" ")) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Condition name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final ConditionID identifier;
                try {
                    identifier = new ConditionID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Error while loading condition '" + packName + "." + key + "': " + e.getMessage());
                    LogUtils.logThrowable(e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.generateInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Condition type not defined in '" + packName + "." + key + "'");
                    LogUtils.logThrowable(e);
                    continue;
                }
                final Class<? extends Condition> conditionClass = conditionTypes.get(type);
                // if it's null then there is no such type registered, log an
                // error
                if (conditionClass == null) {
                    LogUtils.getLogger().log(Level.WARNING, "Condition type " + type + " is not registered,"
                            + " check if it's spelled correctly in '" + identifier + "' condition.");
                    continue;
                }
                try {
                    final Condition condition = conditionClass.getConstructor(Instruction.class)
                            .newInstance(identifier.generateInstruction());
                    conditions.put(identifier, condition);
                    LogUtils.getLogger().log(Level.FINE, "  Condition '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LogUtils.getLogger().log(Level.WARNING,
                                "Error in '" + identifier + "' condition (" + type + "): " + e.getCause().getMessage());
                        LogUtils.logThrowable(e);
                    } else {
                        LogUtils.logThrowableReport(e);
                    }
                } catch (final Exception e) {
                    LogUtils.logThrowableReport(e);
                }
            }
            final FileConfiguration oConfig = pack.getObjectives().getConfig();
            for (final String key : oConfig.getKeys(false)) {
                if (key.contains(" ")) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Objective name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final ObjectiveID identifier;
                try {
                    identifier = new ObjectiveID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Error while loading objective '" + packName + "." + key + "': " + e.getMessage());
                    LogUtils.logThrowable(e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.generateInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Objective type not defined in '" + packName + "." + key + "'");
                    LogUtils.logThrowable(e);
                    continue;
                }
                final Class<? extends Objective> objectiveClass = objectiveTypes.get(type);
                // if it's null then there is no such type registered, log an
                // error
                if (objectiveClass == null) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Objective type " + type + " is not registered, check if it's"
                                    + " spelled correctly in '" + identifier + "' objective.");
                    continue;
                }
                try {
                    final Objective objective = objectiveClass.getConstructor(Instruction.class)
                            .newInstance(identifier.generateInstruction());
                    objectives.put(identifier, objective);
                    LogUtils.getLogger().log(Level.FINE, "  Objective '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LogUtils.getLogger().log(Level.WARNING,
                                "Error in '" + identifier + "' objective (" + type + "): " + e.getCause().getMessage());
                        LogUtils.logThrowable(e);
                    } else {
                        LogUtils.logThrowableReport(e);
                    }
                } catch (final Exception e) {
                    LogUtils.logThrowableReport(e);
                }
            }
            for (final String convName : pack.getConversationNames()) {
                if (convName.contains(" ")) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Conversation name cannot contain spaces: '" + convName + "' (in " + packName
                                    + " package)");
                    continue;
                }
                try {
                    conversations.put(pack.getName() + "." + convName, new ConversationData(pack, convName));
                } catch (final InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Error in '" + packName + "." + convName + "' conversation: " + e.getMessage());
                    LogUtils.logThrowable(e);
                } catch (final Exception e) {
                    LogUtils.logThrowableReport(e);
                }
            }
            // check external pointers
            ConversationData.postEnableCheck();
            LogUtils.getLogger().log(Level.FINE, "Everything in package " + packName + " loaded");
        }
        // done
        LogUtils.getLogger().log(Level.INFO,
                "There are " + conditions.size() + " conditions, " + events.size() + " events, "
                        + objectives.size() + " objectives and " + conversations.size() + " conversations loaded from "
                        + Config.getPackages().size() + " packages.");
        // start those freshly loaded objectives for all players
        for (final PlayerData playerData : playerDataMap.values()) {
            playerData.startObjectives();
        }
        // fire LoadDataEvent
        Bukkit.getPluginManager().callEvent(new LoadDataEvent());
    }

    /**
     * Reloads the plugin.
     */
    public void reload() {
        // reload the configuration
        LogUtils.getLogger().log(Level.FINE, "Reloading configuration");
        new Config();
        // reload updater settings
        BetonQuest.getInstance().getUpdater().reload();
        // load new static events
        new StaticEvents();
        // stop current global locations listener
        // and start new one with reloaded configs
        LogUtils.getLogger().log(Level.FINE, "Restarting global locations");
        new GlobalObjectives();
        new ConversationColors();
        Compatibility.reload();
        // load all events, conditions, objectives, conversations etc.
        loadData();
        // start objectives and update journals for every online player
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final String playerID = PlayerConverter.getID(player);
            LogUtils.getLogger().log(Level.FINE, "Updating journal for player " + PlayerConverter.getName(playerID));
            final PlayerData playerData = instance.getPlayerData(playerID);
            GlobalObjectives.startAll(playerID);
            final Journal journal = playerData.getJournal();
            journal.update();
        }
    }

    @Override
    public void onDisable() {
        // suspend all conversations
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final Conversation conv = Conversation.getConversation(PlayerConverter.getID(player));
            if (conv != null) {
                conv.suspend();
            }
            player.closeInventory();
        }
        // cancel database saver
        saver.end();
        Compatibility.disable();
        database.closeConnection();
        // cancel static events (they are registered outside of Bukkit so it
        // won't happen automatically)
        StaticEvents.stop();
        // done
        LogUtils.getLogger().log(Level.INFO, "BetonQuest succesfully disabled!");
    }

    /**
     * Returns the database instance
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
     * Checks if MySQL is used or not
     *
     * @return if MySQL is used (false means that SQLite is being used)
     */
    public boolean isMySQLUsed() {
        return isMySQLUsed;
    }

    /**
     * Stores the PlayerData in a map, so it can be retrieved using
     * getPlayerData(String playerID)
     *
     * @param playerID   ID of the player
     * @param playerData PlayerData object to store
     */
    public void putPlayerData(final String playerID, final PlayerData playerData) {
        LogUtils.getLogger().log(Level.FINE, "Inserting data for " + PlayerConverter.getName(playerID));
        playerDataMap.put(playerID, playerData);
    }

    /**
     * Retrieves PlayerData object for specified player. If the playerData does
     * not exist but the player is online, it will create new playerData on the
     * main thread and put it into the map.
     *
     * @param playerID ID of the player
     * @return PlayerData object for the player
     */
    public PlayerData getPlayerData(final String playerID) {
        PlayerData playerData = playerDataMap.get(playerID);
        if (playerData == null && PlayerConverter.getPlayer(playerID) != null) {
            playerData = new PlayerData(playerID);
            putPlayerData(playerID, playerData);
        }
        return playerData;
    }

    /**
     * Retrieves GlobalData object which handles all global tags and points
     *
     * @return GlobalData object
     */
    public GlobalData getGlobalData() {
        return globalData;
    }

    /**
     * Removes the database playerData from the map
     *
     * @param playerID ID of the player whose playerData is to be removed
     */
    public void removePlayerData(final String playerID) {
        playerDataMap.remove(playerID);
    }

    /**
     * Registers new condition classes by their names
     *
     * @param name           name of the condition type
     * @param conditionClass class object for the condition
     */
    public void registerConditions(final String name, final Class<? extends Condition> conditionClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " condition type");
        conditionTypes.put(name, conditionClass);
    }

    /**
     * Registers new event classes by their names
     *
     * @param name       name of the event type
     * @param eventClass class object for the condition
     */
    public void registerEvents(final String name, final Class<? extends QuestEvent> eventClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " event type");
        eventTypes.put(name, eventClass);
    }

    /**
     * Registers new objective classes by their names
     *
     * @param name           name of the objective type
     * @param objectiveClass class object for the objective
     */
    public void registerObjectives(final String name, final Class<? extends Objective> objectiveClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " objective type");
        objectiveTypes.put(name, objectiveClass);
    }

    /**
     * Registers new conversation input/output class.
     *
     * @param name        name of the IO type
     * @param convIOClass class object to register
     */
    public void registerConversationIO(final String name, final Class<? extends ConversationIO> convIOClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " conversation IO type");
        convIOTypes.put(name, convIOClass);
    }

    /**
     * Registers new interceptor class.
     *
     * @param name             name of the interceptor type
     * @param interceptorClass class object to register
     */
    public void registerInterceptor(final String name, final Class<? extends Interceptor> interceptorClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " interceptor type");
        interceptorTypes.put(name, interceptorClass);
    }

    /**
     * Registers new notify input/output class.
     *
     * @param name    name of the IO type
     * @param ioClass class object to register
     */
    public void registerNotifyIO(final String name, final Class<? extends NotifyIO> ioClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " notify IO type");
        notifyIOTypes.put(name, ioClass);
    }

    /**
     * Registers new variable type.
     *
     * @param name     name of the variable type
     * @param variable class object of this type
     */
    public void registerVariable(final String name, final Class<? extends Variable> variable) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " variable type");
        variableTypes.put(name, variable);
    }

    /**
     * Returns the list of objectives of this player
     *
     * @param playerID ID of the player
     * @return list of this player's active objectives
     */
    public ArrayList<Objective> getPlayerObjectives(final String playerID) {
        final ArrayList<Objective> list = new ArrayList<>();
        for (final Objective objective : objectives.values()) {
            if (objective.containsPlayer(playerID)) {
                list.add(objective);
            }
        }
        return list;
    }

    /**
     * @param name package name, dot and name of the conversation
     * @return ConversationData object for this conversation or null if it does
     * not exist
     */
    public ConversationData getConversation(final String name) {
        return conversations.get(name);
    }

    /**
     * @param objectiveID package name, dot and ID of the objective
     * @return Objective object or null if it does not exist
     */
    public Objective getObjective(final ObjectiveID objectiveID) {
        for (final Entry<ObjectiveID, Objective> e : objectives.entrySet()) {
            if (e.getKey().equals(objectiveID)) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the instance of Saver
     *
     * @return the Saver
     */
    public Saver getSaver() {
        return saver;
    }

    /**
     * @param name name of the conversation IO type
     * @return the class object for this conversation IO type
     */
    public Class<? extends ConversationIO> getConvIO(final String name) {
        return convIOTypes.get(name);
    }

    /**
     * @param name name of the interceptor type
     * @return the class object for this interceptor type
     */
    public Class<? extends Interceptor> getInterceptor(final String name) {
        return interceptorTypes.get(name);
    }

    /**
     * Resoles the variable for specified player. If the variable is not loaded
     * yet it will load it on the main thread.
     *
     * @param packName name of the package
     * @param name     name of the variable (instruction, with % characters)
     * @param playerID ID of the player
     * @return the value of this variable for given player
     */
    public String getVariableValue(final String packName, final String name, final String playerID) {
        try {
            final Variable var = createVariable(Config.getPackages().get(packName), name);
            if (var == null) {
                return "could not resolve variable";
            }
            return var.getValue(playerID);
        } catch (final InstructionParseException e) {
            LogUtils.getLogger().log(Level.WARNING, "Could not create variable: " + e.getMessage());
            LogUtils.logThrowable(e);
            return "could not resolve variable";
        }
    }

    /**
     * @param name the name of the event class, as previously registered
     * @return the class of the event
     */
    public Class<? extends QuestEvent> getEventClass(final String name) {
        return eventTypes.get(name);
    }

    /**
     * @param name the name of the condition class, as previously registered
     * @return the class of the event
     */
    public Class<? extends Condition> getConditionClass(final String name) {
        return conditionTypes.get(name);
    }

    /**
     * Renames the objective instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        objectives.put(rename, objectives.remove(name));
    }
}
