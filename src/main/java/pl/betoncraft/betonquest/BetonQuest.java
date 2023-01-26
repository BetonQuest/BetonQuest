package pl.betoncraft.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.papermc.lib.PaperLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.LoadDataEvent;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.commands.BackpackCommand;
import pl.betoncraft.betonquest.commands.CancelQuestCommand;
import pl.betoncraft.betonquest.commands.CompassCommand;
import pl.betoncraft.betonquest.commands.JournalCommand;
import pl.betoncraft.betonquest.commands.LangCommand;
import pl.betoncraft.betonquest.commands.QuestCommand;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.conditions.AdvancementCondition;
import pl.betoncraft.betonquest.conditions.AlternativeCondition;
import pl.betoncraft.betonquest.conditions.ArmorCondition;
import pl.betoncraft.betonquest.conditions.ArmorRatingCondition;
import pl.betoncraft.betonquest.conditions.BiomeCondition;
import pl.betoncraft.betonquest.conditions.CheckCondition;
import pl.betoncraft.betonquest.conditions.ChestItemCondition;
import pl.betoncraft.betonquest.conditions.ConjunctionCondition;
import pl.betoncraft.betonquest.conditions.ConversationCondition;
import pl.betoncraft.betonquest.conditions.DayOfWeekCondition;
import pl.betoncraft.betonquest.conditions.EffectCondition;
import pl.betoncraft.betonquest.conditions.EmptySlotsCondition;
import pl.betoncraft.betonquest.conditions.EntityCondition;
import pl.betoncraft.betonquest.conditions.ExperienceCondition;
import pl.betoncraft.betonquest.conditions.FacingCondition;
import pl.betoncraft.betonquest.conditions.FlyingCondition;
import pl.betoncraft.betonquest.conditions.GameModeCondition;
import pl.betoncraft.betonquest.conditions.GlobalPointCondition;
import pl.betoncraft.betonquest.conditions.GlobalTagCondition;
import pl.betoncraft.betonquest.conditions.HandCondition;
import pl.betoncraft.betonquest.conditions.HealthCondition;
import pl.betoncraft.betonquest.conditions.HeightCondition;
import pl.betoncraft.betonquest.conditions.ItemCondition;
import pl.betoncraft.betonquest.conditions.JournalCondition;
import pl.betoncraft.betonquest.conditions.LocationCondition;
import pl.betoncraft.betonquest.conditions.LookingAtCondition;
import pl.betoncraft.betonquest.conditions.MooncycleCondition;
import pl.betoncraft.betonquest.conditions.ObjectiveCondition;
import pl.betoncraft.betonquest.conditions.PartialDateCondition;
import pl.betoncraft.betonquest.conditions.PartyCondition;
import pl.betoncraft.betonquest.conditions.PermissionCondition;
import pl.betoncraft.betonquest.conditions.PointCondition;
import pl.betoncraft.betonquest.conditions.RandomCondition;
import pl.betoncraft.betonquest.conditions.RealTimeCondition;
import pl.betoncraft.betonquest.conditions.ScoreboardCondition;
import pl.betoncraft.betonquest.conditions.SneakCondition;
import pl.betoncraft.betonquest.conditions.TagCondition;
import pl.betoncraft.betonquest.conditions.TestForBlockCondition;
import pl.betoncraft.betonquest.conditions.TimeCondition;
import pl.betoncraft.betonquest.conditions.VariableCondition;
import pl.betoncraft.betonquest.conditions.VehicleCondition;
import pl.betoncraft.betonquest.conditions.WeatherCondition;
import pl.betoncraft.betonquest.conditions.WorldCondition;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.config.ConfigUpdater;
import pl.betoncraft.betonquest.conversation.CombatTagger;
import pl.betoncraft.betonquest.conversation.Conversation;
import pl.betoncraft.betonquest.conversation.ConversationColors;
import pl.betoncraft.betonquest.conversation.ConversationData;
import pl.betoncraft.betonquest.conversation.ConversationIO;
import pl.betoncraft.betonquest.conversation.ConversationResumer;
import pl.betoncraft.betonquest.conversation.Interceptor;
import pl.betoncraft.betonquest.conversation.InventoryConvIO;
import pl.betoncraft.betonquest.conversation.NonInterceptingInterceptor;
import pl.betoncraft.betonquest.conversation.SimpleConvIO;
import pl.betoncraft.betonquest.conversation.SimpleInterceptor;
import pl.betoncraft.betonquest.conversation.SlowTellrawConvIO;
import pl.betoncraft.betonquest.conversation.TellrawConvIO;
import pl.betoncraft.betonquest.database.Database;
import pl.betoncraft.betonquest.database.GlobalData;
import pl.betoncraft.betonquest.database.MySQL;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.database.SQLite;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.events.CancelEvent;
import pl.betoncraft.betonquest.events.ChatEvent;
import pl.betoncraft.betonquest.events.ChestClearEvent;
import pl.betoncraft.betonquest.events.ChestGiveEvent;
import pl.betoncraft.betonquest.events.ChestTakeEvent;
import pl.betoncraft.betonquest.events.ClearEvent;
import pl.betoncraft.betonquest.events.CommandEvent;
import pl.betoncraft.betonquest.events.CompassEvent;
import pl.betoncraft.betonquest.events.ConversationEvent;
import pl.betoncraft.betonquest.events.DamageEvent;
import pl.betoncraft.betonquest.events.DelEffectEvent;
import pl.betoncraft.betonquest.events.DeletePointEvent;
import pl.betoncraft.betonquest.events.DoorEvent;
import pl.betoncraft.betonquest.events.EffectEvent;
import pl.betoncraft.betonquest.events.ExperienceEvent;
import pl.betoncraft.betonquest.events.ExplosionEvent;
import pl.betoncraft.betonquest.events.FolderEvent;
import pl.betoncraft.betonquest.events.GiveEvent;
import pl.betoncraft.betonquest.events.GiveJournalEvent;
import pl.betoncraft.betonquest.events.GlobalPointEvent;
import pl.betoncraft.betonquest.events.GlobalTagEvent;
import pl.betoncraft.betonquest.events.IfElseEvent;
import pl.betoncraft.betonquest.events.JournalEvent;
import pl.betoncraft.betonquest.events.KillEvent;
import pl.betoncraft.betonquest.events.KillMobEvent;
import pl.betoncraft.betonquest.events.LanguageEvent;
import pl.betoncraft.betonquest.events.LeverEvent;
import pl.betoncraft.betonquest.events.LightningEvent;
import pl.betoncraft.betonquest.events.MessageEvent;
import pl.betoncraft.betonquest.events.NotifyAllEvent;
import pl.betoncraft.betonquest.events.NotifyEvent;
import pl.betoncraft.betonquest.events.ObjectiveEvent;
import pl.betoncraft.betonquest.events.OpSudoEvent;
import pl.betoncraft.betonquest.events.PartyEvent;
import pl.betoncraft.betonquest.events.PickRandomEvent;
import pl.betoncraft.betonquest.events.PlaysoundEvent;
import pl.betoncraft.betonquest.events.PointEvent;
import pl.betoncraft.betonquest.events.RunEvent;
import pl.betoncraft.betonquest.events.ScoreboardEvent;
import pl.betoncraft.betonquest.events.SetBlockEvent;
import pl.betoncraft.betonquest.events.SpawnMobEvent;
import pl.betoncraft.betonquest.events.SudoEvent;
import pl.betoncraft.betonquest.events.TagEvent;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.events.TeleportEvent;
import pl.betoncraft.betonquest.events.TimeEvent;
import pl.betoncraft.betonquest.events.TitleEvent;
import pl.betoncraft.betonquest.events.VariableEvent;
import pl.betoncraft.betonquest.events.WeatherEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.id.VariableID;
import pl.betoncraft.betonquest.item.QuestItemHandler;
import pl.betoncraft.betonquest.mechanics.PlayerHider;
import pl.betoncraft.betonquest.notify.ActionBarNotifyIO;
import pl.betoncraft.betonquest.notify.AdvancementNotifyIO;
import pl.betoncraft.betonquest.notify.BossBarNotifyIO;
import pl.betoncraft.betonquest.notify.ChatNotifyIO;
import pl.betoncraft.betonquest.notify.Notify;
import pl.betoncraft.betonquest.notify.NotifyIO;
import pl.betoncraft.betonquest.notify.SoundIO;
import pl.betoncraft.betonquest.notify.SubTitleNotifyIO;
import pl.betoncraft.betonquest.notify.SuppressNotifyIO;
import pl.betoncraft.betonquest.notify.TitleNotifyIO;
import pl.betoncraft.betonquest.objectives.ActionObjective;
import pl.betoncraft.betonquest.objectives.ArrowShootObjective;
import pl.betoncraft.betonquest.objectives.BlockObjective;
import pl.betoncraft.betonquest.objectives.BreedObjective;
import pl.betoncraft.betonquest.objectives.BrewObjective;
import pl.betoncraft.betonquest.objectives.ChestPutObjective;
import pl.betoncraft.betonquest.objectives.ConsumeObjective;
import pl.betoncraft.betonquest.objectives.CraftingObjective;
import pl.betoncraft.betonquest.objectives.DelayObjective;
import pl.betoncraft.betonquest.objectives.DieObjective;
import pl.betoncraft.betonquest.objectives.EnchantObjective;
import pl.betoncraft.betonquest.objectives.EntityInteractObjective;
import pl.betoncraft.betonquest.objectives.ExperienceObjective;
import pl.betoncraft.betonquest.objectives.FishObjective;
import pl.betoncraft.betonquest.objectives.JumpObjective;
import pl.betoncraft.betonquest.objectives.KillPlayerObjective;
import pl.betoncraft.betonquest.objectives.LocationObjective;
import pl.betoncraft.betonquest.objectives.LoginObjective;
import pl.betoncraft.betonquest.objectives.LogoutObjective;
import pl.betoncraft.betonquest.objectives.MobKillObjective;
import pl.betoncraft.betonquest.objectives.PasswordObjective;
import pl.betoncraft.betonquest.objectives.PickupObjective;
import pl.betoncraft.betonquest.objectives.RespawnObjective;
import pl.betoncraft.betonquest.objectives.ShearObjective;
import pl.betoncraft.betonquest.objectives.SmeltingObjective;
import pl.betoncraft.betonquest.objectives.StepObjective;
import pl.betoncraft.betonquest.objectives.TameObjective;
import pl.betoncraft.betonquest.objectives.VariableObjective;
import pl.betoncraft.betonquest.objectives.VehicleObjective;
import pl.betoncraft.betonquest.utils.BStatsMetrics;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;
import pl.betoncraft.betonquest.utils.updater.UpdateDownloader;
import pl.betoncraft.betonquest.utils.updater.UpdateSourceHandler;
import pl.betoncraft.betonquest.utils.updater.Updater;
import pl.betoncraft.betonquest.utils.updater.source.DevelopmentUpdateSource;
import pl.betoncraft.betonquest.utils.updater.source.ReleaseUpdateSource;
import pl.betoncraft.betonquest.utils.updater.source.implementations.GitHubReleaseSource;
import pl.betoncraft.betonquest.utils.updater.source.implementations.NexusReleaseAndDevelopmentSource;
import pl.betoncraft.betonquest.utils.versioning.Version;
import pl.betoncraft.betonquest.variables.ConditionVariable;
import pl.betoncraft.betonquest.variables.GlobalPointVariable;
import pl.betoncraft.betonquest.variables.ItemAmountVariable;
import pl.betoncraft.betonquest.variables.LocationVariable;
import pl.betoncraft.betonquest.variables.MathVariable;
import pl.betoncraft.betonquest.variables.NpcNameVariable;
import pl.betoncraft.betonquest.variables.ObjectivePropertyVariable;
import pl.betoncraft.betonquest.variables.PlayerNameVariable;
import pl.betoncraft.betonquest.variables.PointVariable;
import pl.betoncraft.betonquest.variables.VersionVariable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
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
    private static BetonQuest instance;
    private final ConcurrentHashMap<String, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private String pluginTag;
    private Database database;
    private boolean isMySQLUsed;
    @SuppressWarnings("PMD.DoNotUseThreads")
    private Saver saver;
    private Updater updater;
    private GlobalData globalData;
    private PlayerHider playerHider;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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
                    LogUtils.logThrowableReport(e);
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
            LogUtils.getLogger().log(Level.FINE, "Null condition ID!");
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
        final boolean outcome;
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
        for (final Entry<EventID, QuestEvent> e : EVENTS.entrySet()) {
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
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public static void newObjective(final String playerID, final ObjectiveID objectiveID) {
        // null check
        if (playerID == null || objectiveID == null) {
            LogUtils.getLogger().log(Level.FINE, "Null arguments for the objective!");
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
        for (final Entry<ObjectiveID, Objective> e : OBJECTIVES.entrySet()) {
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
            LogUtils.getLogger().log(Level.FINE, "Variable " + variableID + " loaded");
            return variable;
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in " + variableID + " variable: " + e.getCause().getMessage(), e);
            } else {
                LogUtils.logThrowableReport(e);
            }
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
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

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssCount", "PMD.DoNotUseThreads", "PMD.NPathComplexity"})
    @Override
    public void onEnable() {
        pluginTag = ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + getDescription().getName() + ChatColor.GRAY + "]" + ChatColor.RESET + " ";

        // initialize debugger
        LogUtils.setupLogger();

        // load configuration
        new Config();
        Notify.load();

        // try to connect to database
        final boolean mySQLEnabled = getConfig().getBoolean("mysql.enabled", true);
        if (mySQLEnabled) {
            LogUtils.getLogger().log(Level.FINE, "Connecting to MySQL database");
            this.database = new MySQL(this, getConfig().getString("mysql.host"),
                    getConfig().getString("mysql.port"),
                    getConfig().getString("mysql.base"), getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.pass"));
            if (database.getConnection() != null) {
                isMySQLUsed = true;
                LogUtils.getLogger().log(Level.INFO, "Successfully connected to MySQL database!");
            }
        }
        if (!mySQLEnabled || !isMySQLUsed) {
            this.database = new SQLite(this, "database.db");
            if (mySQLEnabled) {
                LogUtils.getLogger().log(Level.WARNING, "No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                LogUtils.getLogger().log(Level.INFO, "Using SQLite for storing data!");
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
        registerEvents("notifyall", NotifyAllEvent.class);
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
        registerObjectives("login", LoginObjective.class);
        registerObjectives("password", PasswordObjective.class);
        registerObjectives("pickup", PickupObjective.class);
        registerObjectives("fish", FishObjective.class);
        registerObjectives("enchant", EnchantObjective.class);
        registerObjectives("shear", ShearObjective.class);
        registerObjectives("chestput", ChestPutObjective.class);
        registerObjectives("brew", BrewObjective.class);
        registerObjectives("riding", VehicleObjective.class);
        registerObjectives("consume", ConsumeObjective.class);
        registerObjectives("variable", VariableObjective.class);
        registerObjectives("kill", KillPlayerObjective.class);
        registerObjectives("interact", EntityInteractObjective.class);
        registerObjectives("respawn", RespawnObjective.class);
        registerObjectives("breed", BreedObjective.class);
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


                try {
                    playerHider = new PlayerHider();
                } catch (final InstructionParseException e) {
                    LogUtils.getLogger().log(Level.SEVERE, "Could not start PlayerHider! " + e.getMessage(), e);
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
        new BStatsMetrics(this, CONDITIONS, EVENTS, OBJECTIVES, VARIABLES, CONDITION_TYPES, EVENT_TYPES, OBJECTIVE_TYPES, VARIABLE_TYPES);

        final Version pluginVersion = new Version(this.getDescription().getVersion());
        final File updateFolder = getServer().getUpdateFolderFile();
        final File tempFile = new File(updateFolder, this.getFile().getName() + ".temp");
        final File finalFile = new File(updateFolder, this.getFile().getName());
        final UpdateDownloader updateDownloader = new UpdateDownloader(updateFolder.getParentFile().toURI(), tempFile, finalFile);
        final GitHubReleaseSource gitHubReleaseSource = new GitHubReleaseSource("https://api.github.com/repos/BetonQuest/BetonQuest");
        final NexusReleaseAndDevelopmentSource nexusReleaseAndDevelopmentSource = new NexusReleaseAndDevelopmentSource("https://betonquest.org/nexus");
        final List<ReleaseUpdateSource> releaseHandlers = Arrays.asList(gitHubReleaseSource, nexusReleaseAndDevelopmentSource);
        final List<DevelopmentUpdateSource> developmentHandlers = Collections.singletonList(nexusReleaseAndDevelopmentSource);
        final UpdateSourceHandler updateSourceHandler = new UpdateSourceHandler(releaseHandlers, developmentHandlers);
        updater = new Updater(getConfig(), pluginVersion, updateSourceHandler, updateDownloader, this,
                getServer().getScheduler());

        // done
        LogUtils.getLogger().log(Level.INFO, "BetonQuest succesfully enabled!");
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
                final Class<? extends QuestEvent> eventClass = EVENT_TYPES.get(type);
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
                    EVENTS.put(identifier, event);
                    LogUtils.getLogger().log(Level.FINE, "  Event '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LogUtils.getLogger().log(Level.WARNING,
                                "Error in '" + identifier + "' event (" + type + "): " + e.getCause().getMessage());
                        LogUtils.logThrowable(e);
                    } else {
                        LogUtils.logThrowableReport(e);
                    }
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
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
                final Class<? extends Condition> conditionClass = CONDITION_TYPES.get(type);
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
                    CONDITIONS.put(identifier, condition);
                    LogUtils.getLogger().log(Level.FINE, "  Condition '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LogUtils.getLogger().log(Level.WARNING,
                                "Error in '" + identifier + "' condition (" + type + "): " + e.getCause().getMessage());
                        LogUtils.logThrowable(e);
                    } else {
                        LogUtils.logThrowableReport(e);
                    }
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
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
                final Class<? extends Objective> objectiveClass = OBJECTIVE_TYPES.get(type);
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
                    OBJECTIVES.put(identifier, objective);
                    LogUtils.getLogger().log(Level.FINE, "  Objective '" + identifier + "' loaded");
                } catch (final InvocationTargetException e) {
                    if (e.getCause() instanceof InstructionParseException) {
                        LogUtils.getLogger().log(Level.WARNING,
                                "Error in '" + identifier + "' objective (" + type + "): " + e.getCause().getMessage());
                        LogUtils.logThrowable(e);
                    } else {
                        LogUtils.logThrowableReport(e);
                    }
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
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
                    CONVERSATIONS.put(pack.getName() + "." + convName, new ConversationData(pack, convName));
                } catch (final InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING,
                            "Error in '" + packName + "." + convName + "' conversation: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }
            // check external pointers
            ConversationData.postEnableCheck();
            LogUtils.getLogger().log(Level.FINE, "Everything in package " + packName + " loaded");
        }
        // done
        LogUtils.getLogger().log(Level.INFO,
                "There are " + CONDITIONS.size() + " conditions, " + EVENTS.size() + " events, "
                        + OBJECTIVES.size() + " objectives and " + CONVERSATIONS.size() + " conversations loaded from "
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
        Notify.load();
        // load new static events
        new StaticEvents();
        // stop current global locations listener
        // and start new one with reloaded configs
        LogUtils.getLogger().log(Level.FINE, "Restarting global locations");
        new GlobalObjectives();
        ConversationColors.loadColors();
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
        if (playerHider != null) {
            playerHider.stop();
        }
        try {
            playerHider = new PlayerHider();
        } catch (final InstructionParseException e) {
            LogUtils.getLogger().log(Level.SEVERE, "Could not start PlayerHider! " + e.getMessage(), e);
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
        if (playerHider != null) {
            playerHider.stop();
        }

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
        CONDITION_TYPES.put(name, conditionClass);
    }

    /**
     * Registers new event classes by their names
     *
     * @param name       name of the event type
     * @param eventClass class object for the condition
     */
    public void registerEvents(final String name, final Class<? extends QuestEvent> eventClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " event type");
        EVENT_TYPES.put(name, eventClass);
    }

    /**
     * Registers new objective classes by their names
     *
     * @param name           name of the objective type
     * @param objectiveClass class object for the objective
     */
    public void registerObjectives(final String name, final Class<? extends Objective> objectiveClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " objective type");
        OBJECTIVE_TYPES.put(name, objectiveClass);
    }

    /**
     * Registers new conversation input/output class.
     *
     * @param name        name of the IO type
     * @param convIOClass class object to register
     */
    public void registerConversationIO(final String name, final Class<? extends ConversationIO> convIOClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " conversation IO type");
        CONVERSATION_IO_TYPES.put(name, convIOClass);
    }

    /**
     * Registers new interceptor class.
     *
     * @param name             name of the interceptor type
     * @param interceptorClass class object to register
     */
    public void registerInterceptor(final String name, final Class<? extends Interceptor> interceptorClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " interceptor type");
        INTERCEPTOR_TYPES.put(name, interceptorClass);
    }

    /**
     * Registers new notify input/output class.
     *
     * @param name    name of the IO type
     * @param ioClass class object to register
     */
    public void registerNotifyIO(final String name, final Class<? extends NotifyIO> ioClass) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " notify IO type");
        NOTIFY_IO_TYPES.put(name, ioClass);
    }

    /**
     * Registers new variable type.
     *
     * @param name     name of the variable type
     * @param variable class object of this type
     */
    public void registerVariable(final String name, final Class<? extends Variable> variable) {
        LogUtils.getLogger().log(Level.FINE, "Registering " + name + " variable type");
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
            LogUtils.getLogger().log(Level.WARNING, "Variable '" + name + "' contains the non-existent package '" + packName + "' !");
            return "";
        }
        try {
            final Variable var = createVariable(Config.getPackages().get(packName), name);
            if (var == null) {
                LogUtils.getLogger().log(Level.WARNING, "Could not resolve variable '" + name + "'.");
                return "";
            }
            return var.getValue(playerID);
        } catch (final InstructionParseException e) {
            LogUtils.getLogger().log(Level.WARNING, "&cCould not create variable '" + name + "': " + e.getMessage());
            LogUtils.logThrowable(e);
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
