package org.betonquest.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.papermc.lib.PaperLib;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.LoadDataEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.commands.BackpackCommand;
import org.betonquest.betonquest.commands.CancelQuestCommand;
import org.betonquest.betonquest.commands.CompassCommand;
import org.betonquest.betonquest.commands.JournalCommand;
import org.betonquest.betonquest.commands.LangCommand;
import org.betonquest.betonquest.commands.QuestCommand;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.protocollib.FreezeEvent;
import org.betonquest.betonquest.conditions.AdvancementCondition;
import org.betonquest.betonquest.conditions.AlternativeCondition;
import org.betonquest.betonquest.conditions.ArmorCondition;
import org.betonquest.betonquest.conditions.ArmorRatingCondition;
import org.betonquest.betonquest.conditions.BiomeCondition;
import org.betonquest.betonquest.conditions.CheckCondition;
import org.betonquest.betonquest.conditions.ChestItemCondition;
import org.betonquest.betonquest.conditions.ConjunctionCondition;
import org.betonquest.betonquest.conditions.ConversationCondition;
import org.betonquest.betonquest.conditions.DayOfWeekCondition;
import org.betonquest.betonquest.conditions.EffectCondition;
import org.betonquest.betonquest.conditions.EmptySlotsCondition;
import org.betonquest.betonquest.conditions.EntityCondition;
import org.betonquest.betonquest.conditions.ExperienceCondition;
import org.betonquest.betonquest.conditions.FacingCondition;
import org.betonquest.betonquest.conditions.FlyingCondition;
import org.betonquest.betonquest.conditions.GameModeCondition;
import org.betonquest.betonquest.conditions.GlobalPointCondition;
import org.betonquest.betonquest.conditions.GlobalTagCondition;
import org.betonquest.betonquest.conditions.HandCondition;
import org.betonquest.betonquest.conditions.HealthCondition;
import org.betonquest.betonquest.conditions.HeightCondition;
import org.betonquest.betonquest.conditions.ItemCondition;
import org.betonquest.betonquest.conditions.JournalCondition;
import org.betonquest.betonquest.conditions.LocationCondition;
import org.betonquest.betonquest.conditions.LookingAtCondition;
import org.betonquest.betonquest.conditions.MooncycleCondition;
import org.betonquest.betonquest.conditions.ObjectiveCondition;
import org.betonquest.betonquest.conditions.PartialDateCondition;
import org.betonquest.betonquest.conditions.PartyCondition;
import org.betonquest.betonquest.conditions.PermissionCondition;
import org.betonquest.betonquest.conditions.PointCondition;
import org.betonquest.betonquest.conditions.RandomCondition;
import org.betonquest.betonquest.conditions.RealTimeCondition;
import org.betonquest.betonquest.conditions.RideCondition;
import org.betonquest.betonquest.conditions.ScoreboardCondition;
import org.betonquest.betonquest.conditions.SneakCondition;
import org.betonquest.betonquest.conditions.TagCondition;
import org.betonquest.betonquest.conditions.TestForBlockCondition;
import org.betonquest.betonquest.conditions.TimeCondition;
import org.betonquest.betonquest.conditions.VariableCondition;
import org.betonquest.betonquest.conditions.WeatherCondition;
import org.betonquest.betonquest.conditions.WorldCondition;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
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
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.events.CancelEvent;
import org.betonquest.betonquest.events.ChatEvent;
import org.betonquest.betonquest.events.ChestClearEvent;
import org.betonquest.betonquest.events.ChestGiveEvent;
import org.betonquest.betonquest.events.ChestTakeEvent;
import org.betonquest.betonquest.events.ClearEvent;
import org.betonquest.betonquest.events.CommandEvent;
import org.betonquest.betonquest.events.CompassEvent;
import org.betonquest.betonquest.events.ConversationEvent;
import org.betonquest.betonquest.events.DamageEvent;
import org.betonquest.betonquest.events.DelEffectEvent;
import org.betonquest.betonquest.events.DeletePointEvent;
import org.betonquest.betonquest.events.DoorEvent;
import org.betonquest.betonquest.events.EffectEvent;
import org.betonquest.betonquest.events.ExperienceEvent;
import org.betonquest.betonquest.events.ExplosionEvent;
import org.betonquest.betonquest.events.FolderEvent;
import org.betonquest.betonquest.events.GiveEvent;
import org.betonquest.betonquest.events.GiveJournalEvent;
import org.betonquest.betonquest.events.GlobalPointEvent;
import org.betonquest.betonquest.events.GlobalTagEvent;
import org.betonquest.betonquest.events.IfElseEvent;
import org.betonquest.betonquest.events.JournalEvent;
import org.betonquest.betonquest.events.KillEvent;
import org.betonquest.betonquest.events.KillMobEvent;
import org.betonquest.betonquest.events.LanguageEvent;
import org.betonquest.betonquest.events.LeverEvent;
import org.betonquest.betonquest.events.LightningEvent;
import org.betonquest.betonquest.events.NotifyAllEvent;
import org.betonquest.betonquest.events.NotifyEvent;
import org.betonquest.betonquest.events.ObjectiveEvent;
import org.betonquest.betonquest.events.OpSudoEvent;
import org.betonquest.betonquest.events.PartyEvent;
import org.betonquest.betonquest.events.PickRandomEvent;
import org.betonquest.betonquest.events.PointEvent;
import org.betonquest.betonquest.events.RunEvent;
import org.betonquest.betonquest.events.ScoreboardEvent;
import org.betonquest.betonquest.events.SetBlockEvent;
import org.betonquest.betonquest.events.SpawnMobEvent;
import org.betonquest.betonquest.events.SudoEvent;
import org.betonquest.betonquest.events.TagEvent;
import org.betonquest.betonquest.events.TakeEvent;
import org.betonquest.betonquest.events.TeleportEvent;
import org.betonquest.betonquest.events.TimeEvent;
import org.betonquest.betonquest.events.VariableEvent;
import org.betonquest.betonquest.events.WeatherEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.mechanics.PlayerHider;
import org.betonquest.betonquest.menu.RPGMenu;
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
import org.betonquest.betonquest.objectives.ActionObjective;
import org.betonquest.betonquest.objectives.ArrowShootObjective;
import org.betonquest.betonquest.objectives.BlockObjective;
import org.betonquest.betonquest.objectives.BreedObjective;
import org.betonquest.betonquest.objectives.BrewObjective;
import org.betonquest.betonquest.objectives.ChestPutObjective;
import org.betonquest.betonquest.objectives.CommandObjective;
import org.betonquest.betonquest.objectives.ConsumeObjective;
import org.betonquest.betonquest.objectives.CraftingObjective;
import org.betonquest.betonquest.objectives.DelayObjective;
import org.betonquest.betonquest.objectives.DieObjective;
import org.betonquest.betonquest.objectives.EnchantObjective;
import org.betonquest.betonquest.objectives.EntityInteractObjective;
import org.betonquest.betonquest.objectives.ExperienceObjective;
import org.betonquest.betonquest.objectives.FishObjective;
import org.betonquest.betonquest.objectives.JumpObjective;
import org.betonquest.betonquest.objectives.KillPlayerObjective;
import org.betonquest.betonquest.objectives.LocationObjective;
import org.betonquest.betonquest.objectives.LoginObjective;
import org.betonquest.betonquest.objectives.LogoutObjective;
import org.betonquest.betonquest.objectives.MobKillObjective;
import org.betonquest.betonquest.objectives.PasswordObjective;
import org.betonquest.betonquest.objectives.PickupObjective;
import org.betonquest.betonquest.objectives.RespawnObjective;
import org.betonquest.betonquest.objectives.RideObjective;
import org.betonquest.betonquest.objectives.ShearObjective;
import org.betonquest.betonquest.objectives.SmeltingObjective;
import org.betonquest.betonquest.objectives.StepObjective;
import org.betonquest.betonquest.objectives.TameObjective;
import org.betonquest.betonquest.objectives.VariableObjective;
import org.betonquest.betonquest.utils.BStatsMetrics;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.logger.LogWatcher;
import org.betonquest.betonquest.utils.versioning.Updater;
import org.betonquest.betonquest.variables.ConditionVariable;
import org.betonquest.betonquest.variables.GlobalPointVariable;
import org.betonquest.betonquest.variables.ItemAmountVariable;
import org.betonquest.betonquest.variables.LocationVariable;
import org.betonquest.betonquest.variables.MathVariable;
import org.betonquest.betonquest.variables.NpcNameVariable;
import org.betonquest.betonquest.variables.ObjectivePropertyVariable;
import org.betonquest.betonquest.variables.PlayerNameVariable;
import org.betonquest.betonquest.variables.PointVariable;
import org.betonquest.betonquest.variables.VersionVariable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents BetonQuest plugin
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.GodClass",
        "PMD.TooManyMethods", "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidFieldNameMatchingMethodName"})
public class BetonQuest extends JavaPlugin {
    private static final Map<String, Class<? extends Condition>> CONDITION_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends QuestEvent>> EVENT_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Objective>> OBJECTIVE_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends ConversationIO>> CONVERSATION_IO_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Interceptor>> INTERCEPTOR_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends NotifyIO>> NOTIFY_IO_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Variable>> VARIABLE_TYPES = new HashMap<>();
    private static final Map<ConditionID, Condition> CONDITIONS = new HashMap<>();
    private static final Map<EventID, QuestEvent> EVENTS = new HashMap<>();
    private static final Map<ObjectiveID, Objective> OBJECTIVES = new HashMap<>();
    private static final Map<String, ConversationData> CONVERSATIONS = new HashMap<>();
    private static final Map<VariableID, Variable> VARIABLES = new HashMap<>();
    /**
     * The BetonQuest Plugin instance.
     * -- GETTER --
     * Get the plugin's instance.
     *
     * @return The plugin's instance.
     */
    @Getter
    private static BetonQuest instance;
    private static BetonQuestLogger LOG;
    private final ConcurrentHashMap<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    /**
     * The adventure instance.
     * -- GETTER --
     * Get the adventure instance.
     *
     * @return The adventure instance.
     */
    @Getter
    private BukkitAudiences adventure;
    private String pluginTag;
    private Database database;
    private boolean isMySQLUsed;
    @SuppressWarnings("PMD.DoNotUseThreads")
    private Saver saver;
    private Updater updater;
    private GlobalData globalData;
    private PlayerHider playerHider;
    @Getter
    private RPGMenu rpgMenu;
    /**
     * -- GETTER --
     * Get the LogWatcher instance.
     *
     * @return The LogWatcher instance.
     */
    @Getter
    private LogWatcher logWatcher;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public BetonQuest() {
        super();
        instance = this;
        LOG = new BetonQuestLogger(this.getLogger(), this.getClass(), null);
    }

    public static boolean conditions(final String playerID, final Collection<ConditionID> conditionIDs) {
        final ConditionID[] ids = new ConditionID[conditionIDs.size()];
        int index = 0;
        for (final ConditionID id : conditionIDs) {
            ids[index++] = id;
        }
        return conditions(playerID, ids);
    }

    public static boolean conditions(final String playerID, final ConditionID... conditionIDs) {
        if (Bukkit.isPrimaryThread()) {
            for (final ConditionID id : conditionIDs) {
                if (!condition(playerID, id)) {
                    return false;
                }
            }
        } else {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID id : conditionIDs) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> condition(playerID, id));
                conditions.add(future);
            }
            for (final CompletableFuture<Boolean> condition : conditions) {
                try {
                    if (!condition.get()) {
                        return false;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOG.reportException(null, e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the condition described by conditionID is met
     *
     * @param conditionID ID of the condition to check, as defined in conditions.yml
     * @param playerID    ID of the player which should be checked
     * @return if the condition is met
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public static boolean condition(final String playerID, final ConditionID conditionID) {
        // null check
        if (conditionID == null) {
            LOG.debug(null, "Null condition ID!");
            return false;
        }
        // get the condition
        Condition condition = null;
        for (final Entry<ConditionID, Condition> e : CONDITIONS.entrySet()) {
            if (e.getKey().equals(conditionID)) {
                condition = e.getValue();
                break;
            }
        }
        if (condition == null) {
            LOG.warning(conditionID.getPackage(), "The condition " + conditionID + " is not defined!");
            return false;
        }
        // check for null player
        if (playerID == null && !condition.isStatic()) {
            LOG.debug(conditionID.getPackage(), "Cannot check non-static condition without a player, returning false");
            return false;
        }
        // check for online player
        if (playerID != null && PlayerConverter.getPlayer(playerID) == null && !condition.isPersistent()) {
            LOG.debug(conditionID.getPackage(), "Player was offline, condition is not persistent, returning false");
            return false;
        }
        // and check if it's met or not
        final boolean outcome;
        try {
            outcome = condition.handle(playerID);
        } catch (final QuestRuntimeException e) {
            LOG.warning(conditionID.getPackage(), "Error while checking '" + conditionID + "' condition: " + e.getMessage(), e);
            return false;
        }
        final boolean isMet = outcome != conditionID.inverted();
        LOG.debug(conditionID.getPackage(),
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
            LOG.debug(null, "Null event ID!");
            return;
        }
        // get the event
        QuestEvent event = null;
        for (final Entry<EventID, QuestEvent> e : EVENTS.entrySet()) {
            if (e.getKey().equals(eventID)) {
                event = e.getValue();
                break;
            }
        }
        if (event == null) {
            LOG.warning(eventID.getPackage(), "Event " + eventID + " is not defined");
            return;
        }
        // fire the event
        if (playerID == null) {
            LOG.debug(eventID.getPackage(), "Firing static event " + eventID);
        } else {
            LOG.debug(eventID.getPackage(),
                    "Firing event " + eventID + " for " + PlayerConverter.getName(playerID));
        }
        try {
            event.fire(playerID);
        } catch (final QuestRuntimeException e) {
            LOG.warning(eventID.getPackage(), "Error while firing '" + eventID + "' event: " + e.getMessage(), e);
        }
    }

    /**
     * Creates new objective for given player
     *
     * @param playerID    ID of the player
     * @param objectiveID ID of the objective
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public static void newObjective(final String playerID, final ObjectiveID objectiveID) {
        // null check
        if (playerID == null || objectiveID == null) {
            LOG.debug(objectiveID.getPackage(), "Null arguments for the objective!");
            return;
        }
        Objective objective = null;
        for (final Entry<ObjectiveID, Objective> e : OBJECTIVES.entrySet()) {
            if (e.getKey().equals(objectiveID)) {
                objective = e.getValue();
                break;
            }
        }
        if (objective.containsPlayer(playerID)) {
            LOG.debug(objectiveID.getPackage(),
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
            LOG.debug(null, "Null arguments for the objective!");
            return;
        }
        Objective objective = null;
        for (final Entry<ObjectiveID, Objective> e : OBJECTIVES.entrySet()) {
            if (e.getKey().equals(objectiveID)) {
                objective = e.getValue();
                break;
            }
        }
        if (objective == null) {
            LOG.warning(objectiveID.getPackage(), "Objective " + objectiveID + " does not exist");
            return;
        }
        if (objective.containsPlayer(playerID)) {
            LOG.debug(objectiveID.getPackage(),
                    "Player " + PlayerConverter.getName(playerID) + " already has the " + objectiveID + " objective!");
            return;
        }
        objective.resumeObjectiveForPlayer(playerID, instruction);
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
        for (final Entry<VariableID, Variable> e : VARIABLES.entrySet()) {
            if (e.getKey().equals(variableID)) {
                return e.getValue();
            }
        }
        final String[] parts = instruction.replace("%", "").split("\\.");
        if (parts.length <= 0) {
            throw new InstructionParseException("Not enough arguments in variable " + variableID);
        }
        final Class<? extends Variable> variableClass = VARIABLE_TYPES.get(parts[0]);
        // if it's null then there is no such type registered, log an error
        if (variableClass == null) {
            throw new InstructionParseException("Variable type " + parts[0] + " is not registered");
        }
        try {
            final Variable variable = variableClass.getConstructor(Instruction.class)
                    .newInstance(new VariableInstruction(pack, null, instruction));
            VARIABLES.put(variableID, variable);
            LOG.debug(pack, "Variable " + variableID + " loaded");
            return variable;
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in " + variableID + " variable: " + e.getCause().getMessage(), e);
            } else {
                LOG.reportException(pack, e);
            }
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            LOG.reportException(pack, e);
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
    public static List<String> resolveVariables(final String text) {
        final List<String> variables = new ArrayList<>();
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
        return NOTIFY_IO_TYPES.get(name);
    }

    public String getPluginTag() {
        return pluginTag;
    }

    public void callSyncBukkitEvent(final Event event) {
        if (getServer().isPrimaryThread()) {
            getServer().getPluginManager().callEvent(event);
        } else {
            getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> getServer().getPluginManager().callEvent(event));
        }
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.DoNotUseThreads", "PMD.NPathComplexity"})
    @Override
    public void onEnable() {
        pluginTag = ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + getDescription().getName() + ChatColor.GRAY + "]" + ChatColor.RESET + " ";

        // initialize debugger
        logWatcher = new LogWatcher(this);

        adventure = BukkitAudiences.create(this);

        // load configuration
        new Config();
        Notify.load();

        // try to connect to database
        final boolean mySQLEnabled = getConfig().getBoolean("mysql.enabled", true);
        if (mySQLEnabled) {
            LOG.debug(null, "Connecting to MySQL database");
            this.database = new MySQL(this, getConfig().getString("mysql.host"),
                    getConfig().getString("mysql.port"),
                    getConfig().getString("mysql.base"), getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.pass"));
            if (database.getConnection() != null) {
                isMySQLUsed = true;
                LOG.info(null, "Successfully connected to MySQL database!");
            }
        }
        if (!mySQLEnabled || !isMySQLUsed) {
            this.database = new SQLite(this, "database.db");
            if (mySQLEnabled) {
                LOG.warning(null, "No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                LOG.info(null, "Using SQLite for storing data!");
            }
        }

        // create tables in the database
        database.createTables(isMySQLUsed);

        // create and start the saver object, which handles correct asynchronous
        // saving to the database
        saver = new Saver();
        saver.start();

        // load database backup
        Utils.loadDatabaseFromBackup();

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
        ConversationColors.loadColors();

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
        registerConditions("ride", RideCondition.class);
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
        registerEvents("language", LanguageEvent.class);
        registerEvents("pickrandom", PickRandomEvent.class);
        registerEvents("experience", ExperienceEvent.class);
        registerEvents("notify", NotifyEvent.class);
        registerEvents("notifyall", NotifyAllEvent.class);
        registerEvents("chat", ChatEvent.class);
        registerEvents("freeze", FreezeEvent.class);

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
        registerObjectives("login", LoginObjective.class);
        registerObjectives("password", PasswordObjective.class);
        registerObjectives("pickup", PickupObjective.class);
        registerObjectives("fish", FishObjective.class);
        registerObjectives("enchant", EnchantObjective.class);
        registerObjectives("shear", ShearObjective.class);
        registerObjectives("chestput", ChestPutObjective.class);
        registerObjectives("brew", BrewObjective.class);
        registerObjectives("ride", RideObjective.class);
        registerObjectives("consume", ConsumeObjective.class);
        registerObjectives("variable", VariableObjective.class);
        registerObjectives("kill", KillPlayerObjective.class);
        registerObjectives("interact", EntityInteractObjective.class);
        registerObjectives("respawn", RespawnObjective.class);
        registerObjectives("breed", BreedObjective.class);
        registerObjectives("command", CommandObjective.class);
        if (PaperLib.isPaper()) {
            registerObjectives("jump", JumpObjective.class);
        }

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
        registerNotifyIO("totem", TotemNotifyIO.class);
        registerNotifyIO("subtitle", SubTitleNotifyIO.class);
        registerNotifyIO("sound", SoundIO.class);

        // register variable types
        registerVariable("condition", ConditionVariable.class);
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
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

            try {
                playerHider = new PlayerHider();
            } catch (final InstructionParseException e) {
                LOG.error(null, "Could not start PlayerHider! " + e.getMessage(), e);
            }
        });

        // block betonquestanswer logging (it's just a spam)
        try {
            Class.forName("org.apache.logging.log4j.core.Filter");
            final Logger coreLogger = (Logger) LogManager.getRootLogger();
            coreLogger.addFilter(new AnswerFilter());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOG.warning(null, "Could not disable /betonquestanswer logging", e);
        }

        // metrics
        new BStatsMetrics(this, CONDITIONS, EVENTS, OBJECTIVES, VARIABLES, CONDITION_TYPES, EVENT_TYPES, OBJECTIVE_TYPES, VARIABLE_TYPES);

        // updater
        updater = new Updater(this.getDescription().getVersion(), this.getFile());

        //RPGMenu integration
        rpgMenu = new RPGMenu();
        rpgMenu.onEnable();

        // done
        LOG.info(null, "BetonQuest succesfully enabled!");
    }

    /**
     * Loads events and conditions to the maps
     */
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.NPathComplexity"})
    public void loadData() {
        // save data of all objectives to the players
        for (final Objective objective : OBJECTIVES.values()) {
            objective.close();
        }
        // clear previously loaded data
        EVENTS.clear();
        CONDITIONS.clear();
        CONVERSATIONS.clear();
        OBJECTIVES.clear();
        VARIABLES.clear();
        // load new data
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String packName = pack.getName();
            LOG.debug(pack, "Loading stuff in package " + packName);
            final FileConfiguration eConfig = Config.getPackages().get(packName).getEvents().getConfig();
            for (final String key : eConfig.getKeys(false)) {
                if (key.contains(" ")) {
                    LOG.warning(pack,
                            "Event name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final EventID identifier;
                try {
                    identifier = new EventID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    LOG.warning(pack, "Error while loading event '" + packName + "." + key + "': " + e.getMessage(), e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.generateInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    LOG.warning(pack, "Objective type not defined in '" + packName + "." + key + "'", e);
                    continue;
                }
                final Class<? extends QuestEvent> eventClass = EVENT_TYPES.get(type);
                if (eventClass == null) {
                    // if it's null then there is no such type registered, log
                    // an error
                    LOG.warning(pack, "Event type " + type + " is not registered, check if it's"
                            + " spelled correctly in '" + identifier + "' event.");
                    continue;
                }
                try {
                    final QuestEvent event = eventClass.getConstructor(Instruction.class)
                            .newInstance(identifier.generateInstruction());
                    EVENTS.put(identifier, event);
                    LOG.debug(pack, "  Event '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LOG.warning(pack, "Error in '" + identifier + "' event (" + type + "): " + e.getCause().getMessage(), e);
                    } else {
                        LOG.reportException(pack, e);
                    }
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    LOG.reportException(pack, e);
                }
            }
            final FileConfiguration cConfig = pack.getConditions().getConfig();
            for (final String key : cConfig.getKeys(false)) {
                if (key.contains(" ")) {
                    LOG.warning(pack,
                            "Condition name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final ConditionID identifier;
                try {
                    identifier = new ConditionID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    LOG.warning(pack, "Error while loading condition '" + packName + "." + key + "': " + e.getMessage(), e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.generateInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    LOG.warning(pack, "Condition type not defined in '" + packName + "." + key + "'", e);
                    continue;
                }
                final Class<? extends Condition> conditionClass = CONDITION_TYPES.get(type);
                // if it's null then there is no such type registered, log an
                // error
                if (conditionClass == null) {
                    LOG.warning(pack, "Condition type " + type + " is not registered,"
                            + " check if it's spelled correctly in '" + identifier + "' condition.");
                    continue;
                }
                try {
                    final Condition condition = conditionClass.getConstructor(Instruction.class)
                            .newInstance(identifier.generateInstruction());
                    CONDITIONS.put(identifier, condition);
                    LOG.debug(pack, "  Condition '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LOG.warning(pack, "Error in '" + identifier + "' condition (" + type + "): " + e.getCause().getMessage(), e);
                    } else {
                        LOG.reportException(pack, e);
                    }
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    LOG.reportException(pack, e);
                }
            }
            final FileConfiguration oConfig = pack.getObjectives().getConfig();
            for (final String key : oConfig.getKeys(false)) {
                if (key.contains(" ")) {
                    LOG.warning(pack,
                            "Objective name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final ObjectiveID identifier;
                try {
                    identifier = new ObjectiveID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    LOG.warning(pack, "Error while loading objective '" + packName + "." + key + "': " + e.getMessage(), e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.generateInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    LOG.warning(pack, "Objective type not defined in '" + packName + "." + key + "'", e);
                    continue;
                }
                final Class<? extends Objective> objectiveClass = OBJECTIVE_TYPES.get(type);
                // if it's null then there is no such type registered, log an
                // error
                if (objectiveClass == null) {
                    LOG.warning(pack,
                            "Objective type " + type + " is not registered, check if it's"
                                    + " spelled correctly in '" + identifier + "' objective.");
                    continue;
                }
                try {
                    final Objective objective = objectiveClass.getConstructor(Instruction.class)
                            .newInstance(identifier.generateInstruction());
                    OBJECTIVES.put(identifier, objective);
                    LOG.debug(pack, "  Objective '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LOG.warning(pack, "Error in '" + identifier + "' objective (" + type + "): " + e.getCause().getMessage(), e);
                    } else {
                        LOG.reportException(pack, e);
                    }
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    LOG.reportException(pack, e);
                }
            }
            for (final String convName : pack.getConversationNames()) {
                if (convName.contains(" ")) {
                    LOG.warning(pack,
                            "Conversation name cannot contain spaces: '" + convName + "' (in " + packName
                                    + " package)");
                    continue;
                }
                try {
                    CONVERSATIONS.put(pack.getName() + "." + convName, new ConversationData(pack, convName));
                } catch (final InstructionParseException e) {
                    LOG.warning(pack, "Error in '" + packName + "." + convName + "' conversation: " + e.getMessage(), e);
                }
            }
            // check external pointers
            ConversationData.postEnableCheck();
            LOG.debug(pack, "Everything in package " + packName + " loaded");
        }
        // done
        LOG.info(null,
                "There are " + CONDITIONS.size() + " conditions, " + EVENTS.size() + " events, "
                        + OBJECTIVES.size() + " objectives and " + CONVERSATIONS.size() + " conversations loaded from "
                        + Config.getPackages().size() + " packages.");
        // start those freshly loaded objectives for all players
        for (final PlayerData playerData : playerDataMap.values()) {
            playerData.startObjectives();
        }

        rpgMenu.reloadData();

        // fire LoadDataEvent
        Bukkit.getPluginManager().callEvent(new LoadDataEvent());
    }

    /**
     * Reloads the plugin.
     */
    public void reload() {
        // reload the configuration
        LOG.debug(null, "Reloading configuration");
        new Config();
        Notify.load();
        // reload updater settings
        BetonQuest.getInstance().getUpdater().searchUpdate();
        // load new static events
        new StaticEvents();
        // stop current global locations listener
        // and start new one with reloaded configs
        LOG.debug(null, "Restarting global locations");
        new GlobalObjectives();
        ConversationColors.loadColors();
        Compatibility.reload();
        // load all events, conditions, objectives, conversations etc.
        loadData();
        // start objectives and update journals for every online player
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final String playerID = PlayerConverter.getID(player);
            LOG.debug(null, "Updating journal for player " + PlayerConverter.getName(playerID));
            final PlayerData playerData = instance.getPlayerData(playerID);
            GlobalObjectives.startAll(playerID);
            final Journal journal = playerData.getJournal();
            journal.update();
        }
        playerHider.stop();
        try {
            playerHider = new PlayerHider();
        } catch (final InstructionParseException e) {
            LOG.error(null, "Could not start PlayerHider! " + e.getMessage(), e);
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
        playerHider.stop();


        // done
        LOG.info(null, "BetonQuest succesfully disabled!");

        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        rpgMenu.onDisable();
        FreezeEvent.cleanup();
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
        LOG.debug(null, "Inserting data for " + PlayerConverter.getName(playerID));
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
        LOG.debug(null, "Registering " + name + " condition type");
        CONDITION_TYPES.put(name, conditionClass);
    }

    /**
     * Registers new event classes by their names
     *
     * @param name       name of the event type
     * @param eventClass class object for the condition
     */
    public void registerEvents(final String name, final Class<? extends QuestEvent> eventClass) {
        LOG.debug(null, "Registering " + name + " event type");
        EVENT_TYPES.put(name, eventClass);
    }

    /**
     * Registers new objective classes by their names
     *
     * @param name           name of the objective type
     * @param objectiveClass class object for the objective
     */
    public void registerObjectives(final String name, final Class<? extends Objective> objectiveClass) {
        LOG.debug(null, "Registering " + name + " objective type");
        OBJECTIVE_TYPES.put(name, objectiveClass);
    }

    /**
     * Registers new conversation input/output class.
     *
     * @param name        name of the IO type
     * @param convIOClass class object to register
     */
    public void registerConversationIO(final String name, final Class<? extends ConversationIO> convIOClass) {
        LOG.debug(null, "Registering " + name + " conversation IO type");
        CONVERSATION_IO_TYPES.put(name, convIOClass);
    }

    /**
     * Registers new interceptor class.
     *
     * @param name             name of the interceptor type
     * @param interceptorClass class object to register
     */
    public void registerInterceptor(final String name, final Class<? extends Interceptor> interceptorClass) {
        LOG.debug(null, "Registering " + name + " interceptor type");
        INTERCEPTOR_TYPES.put(name, interceptorClass);
    }

    /**
     * Registers new notify input/output class.
     *
     * @param name    name of the IO type
     * @param ioClass class object to register
     */
    public void registerNotifyIO(final String name, final Class<? extends NotifyIO> ioClass) {
        LOG.debug(null, "Registering " + name + " notify IO type");
        NOTIFY_IO_TYPES.put(name, ioClass);
    }

    /**
     * Registers new variable type.
     *
     * @param name     name of the variable type
     * @param variable class object of this type
     */
    public void registerVariable(final String name, final Class<? extends Variable> variable) {
        LOG.debug(null, "Registering " + name + " variable type");
        VARIABLE_TYPES.put(name, variable);
    }

    /**
     * Returns the list of objectives of this player
     *
     * @param playerID ID of the player
     * @return list of this player's active objectives
     */
    public List<Objective> getPlayerObjectives(final String playerID) {
        final List<Objective> list = new ArrayList<>();
        for (final Objective objective : OBJECTIVES.values()) {
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
        return CONVERSATIONS.get(name);
    }

    /**
     * @param objectiveID package name, dot and ID of the objective
     * @return Objective object or null if it does not exist
     */
    public Objective getObjective(final ObjectiveID objectiveID) {
        for (final Entry<ObjectiveID, Objective> e : OBJECTIVES.entrySet()) {
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
    @SuppressWarnings("PMD.DoNotUseThreads")
    public Saver getSaver() {
        return saver;
    }

    /**
     * @param name name of the conversation IO type
     * @return the class object for this conversation IO type
     */
    public Class<? extends ConversationIO> getConvIO(final String name) {
        return CONVERSATION_IO_TYPES.get(name);
    }

    /**
     * @param name name of the interceptor type
     * @return the class object for this interceptor type
     */
    public Class<? extends Interceptor> getInterceptor(final String name) {
        return INTERCEPTOR_TYPES.get(name);
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
        if (!Config.getPackages().containsKey(packName)) {
            LOG.warning(null, "Variable '" + name + "' contains the non-existent package '" + packName + "' !");
            return "";
        }
        final ConfigPackage pack = Config.getPackages().get(packName);
        try {
            final Variable var = createVariable(pack, name);
            if (var == null) {
                LOG.warning(pack, "Could not resolve variable '" + name + "'.");
                return "";
            }
            return var.getValue(playerID);
        } catch (final InstructionParseException e) {
            LOG.warning(pack, "&cCould not create variable '" + name + "': " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * @param name the name of the event class, as previously registered
     * @return the class of the event
     */
    public Class<? extends QuestEvent> getEventClass(final String name) {
        return EVENT_TYPES.get(name);
    }

    /**
     * @param name the name of the condition class, as previously registered
     * @return the class of the event
     */
    public Class<? extends Condition> getConditionClass(final String name) {
        return CONDITION_TYPES.get(name);
    }

    /**
     * Renames the objective instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        OBJECTIVES.put(rename, OBJECTIVES.remove(name));
    }
}
