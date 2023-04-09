package org.betonquest.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.LoadDataEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.schedule.Schedule;
import org.betonquest.betonquest.api.schedule.Scheduler;
import org.betonquest.betonquest.bstats.BStatsMetrics;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
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
import org.betonquest.betonquest.conditions.BurningCondition;
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
import org.betonquest.betonquest.conditions.HungerCondition;
import org.betonquest.betonquest.conditions.InConversationCondition;
import org.betonquest.betonquest.conditions.ItemCondition;
import org.betonquest.betonquest.conditions.JournalCondition;
import org.betonquest.betonquest.conditions.LanguageCondition;
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
import org.betonquest.betonquest.events.ChatEvent;
import org.betonquest.betonquest.events.ChestGiveEvent;
import org.betonquest.betonquest.events.ClearEvent;
import org.betonquest.betonquest.events.CommandEvent;
import org.betonquest.betonquest.events.CompassEvent;
import org.betonquest.betonquest.events.FolderEvent;
import org.betonquest.betonquest.events.GiveEvent;
import org.betonquest.betonquest.events.KillMobEvent;
import org.betonquest.betonquest.events.ObjectiveEvent;
import org.betonquest.betonquest.events.OpSudoEvent;
import org.betonquest.betonquest.events.PickRandomEvent;
import org.betonquest.betonquest.events.RunEvent;
import org.betonquest.betonquest.events.SpawnMobEvent;
import org.betonquest.betonquest.events.SudoEvent;
import org.betonquest.betonquest.events.TakeEvent;
import org.betonquest.betonquest.events.VariableEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.item.QuestItemHandler;
import org.betonquest.betonquest.menu.RPGMenu;
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
import org.betonquest.betonquest.modules.web.WebContentSource;
import org.betonquest.betonquest.modules.web.updater.UpdateDownloader;
import org.betonquest.betonquest.modules.web.updater.UpdateSourceHandler;
import org.betonquest.betonquest.modules.web.updater.Updater;
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
import org.betonquest.betonquest.objectives.EquipItemObjective;
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
import org.betonquest.betonquest.quest.event.NullStaticEventFactory;
import org.betonquest.betonquest.quest.event.burn.BurnEventFactory;
import org.betonquest.betonquest.quest.event.cancel.CancelEventFactory;
import org.betonquest.betonquest.quest.event.chest.ChestClearEventFactory;
import org.betonquest.betonquest.quest.event.chest.ChestTakeEventFactory;
import org.betonquest.betonquest.quest.event.conversation.CancelConversationEventFactory;
import org.betonquest.betonquest.quest.event.conversation.ConversationEventFactory;
import org.betonquest.betonquest.quest.event.damage.DamageEventFactory;
import org.betonquest.betonquest.quest.event.door.DoorEventFactory;
import org.betonquest.betonquest.quest.event.effect.DeleteEffectEventFactory;
import org.betonquest.betonquest.quest.event.effect.EffectEventFactory;
import org.betonquest.betonquest.quest.event.experience.ExperienceEventFactory;
import org.betonquest.betonquest.quest.event.explosion.ExplosionEventFactory;
import org.betonquest.betonquest.quest.event.hunger.HungerEventFactory;
import org.betonquest.betonquest.quest.event.journal.GiveJournalEventFactory;
import org.betonquest.betonquest.quest.event.journal.JournalEventFactory;
import org.betonquest.betonquest.quest.event.kill.KillEventFactory;
import org.betonquest.betonquest.quest.event.language.LanguageEventFactory;
import org.betonquest.betonquest.quest.event.legacy.FromClassQuestEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactoryAdapter;
import org.betonquest.betonquest.quest.event.lever.LeverEventFactory;
import org.betonquest.betonquest.quest.event.lightning.LightningEventFactory;
import org.betonquest.betonquest.quest.event.logic.IfElseEventFactory;
import org.betonquest.betonquest.quest.event.notify.NotifyAllEventFactory;
import org.betonquest.betonquest.quest.event.notify.NotifyEventFactory;
import org.betonquest.betonquest.quest.event.party.PartyEventFactory;
import org.betonquest.betonquest.quest.event.point.DeleteGlobalPointEventFactory;
import org.betonquest.betonquest.quest.event.point.DeletePointEventFactory;
import org.betonquest.betonquest.quest.event.point.GlobalPointEventFactory;
import org.betonquest.betonquest.quest.event.point.PointEventFactory;
import org.betonquest.betonquest.quest.event.scoreboard.ScoreboardEventFactory;
import org.betonquest.betonquest.quest.event.setblock.SetBlockEventFactory;
import org.betonquest.betonquest.quest.event.tag.TagGlobalEventFactory;
import org.betonquest.betonquest.quest.event.tag.TagPlayerEventFactory;
import org.betonquest.betonquest.quest.event.teleport.TeleportEventFactory;
import org.betonquest.betonquest.quest.event.time.TimeEventFactory;
import org.betonquest.betonquest.quest.event.velocity.VelocityEventFactory;
import org.betonquest.betonquest.quest.event.weather.WeatherEventFactory;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.variables.ConditionVariable;
import org.betonquest.betonquest.variables.GlobalPointVariable;
import org.betonquest.betonquest.variables.GlobalTagVariable;
import org.betonquest.betonquest.variables.ItemVariable;
import org.betonquest.betonquest.variables.LocationVariable;
import org.betonquest.betonquest.variables.MathVariable;
import org.betonquest.betonquest.variables.NpcNameVariable;
import org.betonquest.betonquest.variables.ObjectivePropertyVariable;
import org.betonquest.betonquest.variables.PlayerNameVariable;
import org.betonquest.betonquest.variables.PointVariable;
import org.betonquest.betonquest.variables.RandomNumberVariable;
import org.betonquest.betonquest.variables.TagVariable;
import org.betonquest.betonquest.variables.VersionVariable;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.InstantSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents BetonQuest plugin.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyMethods",
        "PMD.CommentRequired", "PMD.AvoidDuplicateLiterals", "PMD.AvoidFieldNameMatchingMethodName",
        "PMD.AtLeastOneConstructor", "PMD.ExcessivePublicCount", "PMD.TooManyFields"})
public class BetonQuest extends JavaPlugin {
    private final static int BSTATS_METRICS_ID = 551;

    private static final Map<String, Class<? extends Condition>> CONDITION_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Objective>> OBJECTIVE_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends ConversationIO>> CONVERSATION_IO_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Interceptor>> INTERCEPTOR_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends NotifyIO>> NOTIFY_IO_TYPES = new HashMap<>();
    private static final Map<String, Class<? extends Variable>> VARIABLE_TYPES = new HashMap<>();
    private static final Map<String, EventScheduling.ScheduleType<?>> SCHEDULE_TYPES = new HashMap<>();
    private static final Map<ConditionID, Condition> CONDITIONS = new HashMap<>();
    private static final Map<EventID, QuestEvent> EVENTS = new HashMap<>();
    private static final Map<ObjectiveID, Objective> OBJECTIVES = new HashMap<>();
    private static final Map<String, ConversationData> CONVERSATIONS = new HashMap<>();
    private static final Map<VariableID, Variable> VARIABLES = new HashMap<>();
    private static final Map<String, QuestCanceler> CANCELERS = new HashMap<>();
    /**
     * The BetonQuest Plugin instance.
     */
    private static BetonQuest instance;
    private static BetonQuestLogger log;

    /**
     * Map of registered events.
     */
    private final Map<String, QuestEventFactory> eventTypes = new HashMap<>();
    private final ConcurrentHashMap<Profile, PlayerData> playerDataMap = new ConcurrentHashMap<>();
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
     * Event scheduling module
     */
    private EventScheduling eventScheduling;

    /**
     * Cache for event schedulers, holding the last execution of an event
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

    public static boolean conditions(final Profile profile, final Collection<ConditionID> conditionIDs) {
        final ConditionID[] ids = new ConditionID[conditionIDs.size()];
        int index = 0;
        for (final ConditionID id : conditionIDs) {
            ids[index++] = id;
        }
        return conditions(profile, ids);
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    public static boolean conditions(final Profile profile, final ConditionID... conditionIDs) {
        if (Bukkit.isPrimaryThread()) {
            for (final ConditionID id : conditionIDs) {
                if (!condition(profile, id)) {
                    return false;
                }
            }
        } else {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID id : conditionIDs) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> condition(profile, id));
                conditions.add(future);
            }
            for (final CompletableFuture<Boolean> condition : conditions) {
                try {
                    if (!condition.get()) {
                        return false;
                    }
                } catch (final InterruptedException | ExecutionException e) {
                    // Currently conditions that are forced to be sync cause every CompletableFuture.get() call
                    // to delay the check by one tick.
                    // If this happens during a shutdown, the check will be delayed past the last tick.
                    // This will throw a CancellationException and IllegalPluginAccessExceptions.
                    // For Paper we can detect this and only log it to the debug log.
                    // When the conditions get reworked, this complete check can be removed including the Spigot message.
                    if (PaperLib.isPaper() && Bukkit.getServer().isStopping()) {
                        log.debug("Exception during shutdown while checking conditions (expected):", e);
                        return false;
                    }
                    if (PaperLib.isSpigot()) {
                        log.warn("The following exception is only ok when the server is currently stopping." +
                                "Switch to papermc.io to fix this.");
                    }
                    log.reportException(e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the condition described by conditionID is met
     *
     * @param conditionID ID of the condition to check
     * @param profile     the {@link Profile} of the player which should be checked
     * @return if the condition is met
     */
    @SuppressWarnings("PMD.NPathComplexity")
    public static boolean condition(final Profile profile, final ConditionID conditionID) {
        if (conditionID == null) {
            log.debug("Null condition ID!");
            return false;
        }
        Condition condition = null;
        for (final Entry<ConditionID, Condition> e : CONDITIONS.entrySet()) {
            if (e.getKey().equals(conditionID)) {
                condition = e.getValue();
                break;
            }
        }
        if (condition == null) {
            log.warn(conditionID.getPackage(), "The condition " + conditionID + " is not defined!");
            return false;
        }
        if (profile == null && !condition.isStatic()) {
            log.debug(conditionID.getPackage(), "Cannot check non-static condition without a player, returning false");
            return false;
        }
        if (profile != null && profile.getOnlineProfile().isEmpty() && !condition.isPersistent()) {
            log.debug(conditionID.getPackage(), "Player was offline, condition is not persistent, returning false");
            return false;
        }
        final boolean outcome;
        try {
            outcome = condition.handle(profile);
        } catch (final QuestRuntimeException e) {
            log.warn(conditionID.getPackage(), "Error while checking '" + conditionID + "' condition: " + e.getMessage(), e);
            return false;
        }
        final boolean isMet = outcome != conditionID.inverted();
        log.debug(conditionID.getPackage(),
                (isMet ? "TRUE" : "FALSE") + ": " + (conditionID.inverted() ? "inverted" : "") + " condition "
                        + conditionID + " for " + profile);
        return isMet;
    }

    /**
     * Fires an event for the {@link Profile} if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} for which the event must be executed or null
     * @param eventID ID of the event to fire
     */
    public static void event(@Nullable final Profile profile, final EventID eventID) {
        if (eventID == null) {
            log.debug("Null event ID!");
            return;
        }
        QuestEvent event = null;
        for (final Entry<EventID, QuestEvent> e : EVENTS.entrySet()) {
            if (e.getKey().equals(eventID)) {
                event = e.getValue();
                break;
            }
        }
        if (event == null) {
            log.warn(eventID.getPackage(), "Event " + eventID + " is not defined");
            return;
        }
        if (profile == null) {
            log.debug(eventID.getPackage(), "Firing static event " + eventID);
        } else {
            log.debug(eventID.getPackage(),
                    "Firing event " + eventID + " for " + profile);
        }
        try {
            event.fire(profile);
        } catch (final QuestRuntimeException e) {
            log.warn(eventID.getPackage(), "Error while firing '" + eventID + "' event: " + e.getMessage(), e);
        }
    }

    /**
     * Creates new objective for given player
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public static void newObjective(final Profile profile, final ObjectiveID objectiveID) {
        if (profile == null || objectiveID == null) {
            log.debug(objectiveID.getPackage(), "Null arguments for the objective!");
            return;
        }
        Objective objective = null;
        for (final Entry<ObjectiveID, Objective> e : OBJECTIVES.entrySet()) {
            if (e.getKey().equals(objectiveID)) {
                objective = e.getValue();
                break;
            }
        }
        if (objective.containsPlayer(profile)) {
            log.debug(objectiveID.getPackage(),
                    profile + " already has the " + objectiveID +
                            " objective");
            return;
        }
        objective.newPlayer(profile);
    }

    /**
     * Resumes the existing objective for given player
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    public static void resumeObjective(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        if (profile == null || objectiveID == null || instruction == null) {
            log.debug("Null arguments for the objective!");
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
            log.warn(objectiveID.getPackage(), "Objective " + objectiveID + " does not exist");
            return;
        }
        if (objective.containsPlayer(profile)) {
            log.debug(objectiveID.getPackage(),
                    profile + " already has the " + objectiveID + " objective!");
            return;
        }
        objective.resumeObjectiveForPlayer(profile, instruction);
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
    public static Variable createVariable(final QuestPackage pack, final String instruction)
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
        final Instruction instructionVar = variableID.generateInstruction();
        final Class<? extends Variable> variableClass = VARIABLE_TYPES.get(instructionVar.getPart(0));
        // if it's null then there is no such type registered, log an error
        if (variableClass == null) {
            throw new InstructionParseException("Variable type " + instructionVar.getPart(0) + " is not registered");
        }

        try {
            final Variable variable = variableClass.getConstructor(Instruction.class)
                    .newInstance(new VariableInstruction(variableID.getPackage(), null, "%" + instructionVar.getInstruction() + "%"));
            VARIABLES.put(variableID, variable);
            log.debug(pack, "Variable " + variableID + " loaded");
            return variable;
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof InstructionParseException) {
                throw new InstructionParseException("Error in " + variableID + " variable: " + e.getCause().getMessage(), e);
            } else {
                log.reportException(pack, e);
            }
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            log.reportException(pack, e);
        }
        return null;
    }

    public static boolean isVariableType(final String type) {
        return VARIABLE_TYPES.get(type) != null;
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

    private static void loadQuestCanceler() {
        for (final Entry<String, QuestPackage> entry : Config.getPackages().entrySet()) {
            final QuestPackage pack = entry.getValue();
            final ConfigurationSection cancelSection = pack.getConfig().getConfigurationSection("cancel");
            if (cancelSection != null) {
                for (final String key : cancelSection.getKeys(false)) {
                    try {
                        CANCELERS.put(entry.getKey() + "." + key, new QuestCanceler(pack, key));
                    } catch (final InstructionParseException e) {
                        log.warn(pack, "Could not load '" + pack.getQuestPath() + "." + key + "' quest canceler: " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    public static Map<String, QuestCanceler> getCanceler() {
        return CANCELERS;
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

    @NotNull
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
            getServer().getScheduler().runTask(BetonQuest.getInstance(), () -> getServer().getPluginManager().callEvent(event));
        }
    }

    @SuppressWarnings({"PMD.NcssCount", "PMD.DoNotUseThreads", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    @Override
    public void onEnable() {
        instance = this;
        log = BetonQuestLogger.create(this);
        pluginTag = ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + getDescription().getName() + ChatColor.GRAY + "]" + ChatColor.RESET + " ";

        final JREVersionPrinter jreVersionPrinter = new JREVersionPrinter();
        final String jreInfo = jreVersionPrinter.getMessage();
        log.info(jreInfo);

        try {
            config = ConfigurationFile.create(new File(getDataFolder(), "config.yml"), this, "config.yml");
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            log.error("Could not load the config.yml file!", e);
            return;
        }

        final HistoryHandler debugHistoryHandler = HandlerFactory.createHistoryHandler(this, this.getServer().getScheduler(), config, new File(getDataFolder(), "/logs"), InstantSource.system());
        registerLogHandler(getServer(), debugHistoryHandler);
        adventure = BukkitAudiences.create(this);
        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final ChatHandler chatHandler = HandlerFactory.createChatHandler(this, receiverSelector, adventure);
        registerLogHandler(getServer(), chatHandler);

        final String version = getDescription().getVersion();
        log.debug("BetonQuest " + version + " is starting...");
        log.debug(jreInfo);

        Config.setup(this);
        Notify.load();

        final boolean mySQLEnabled = config.getBoolean("mysql.enabled", true);
        if (mySQLEnabled) {
            log.debug("Connecting to MySQL database");
            this.database = new MySQL(this, config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.base"),
                    config.getString("mysql.user"),
                    config.getString("mysql.pass"));
            if (database.getConnection() != null) {
                isMySQLUsed = true;
                log.info("Successfully connected to MySQL database!");
            }
        }
        if (!mySQLEnabled || !isMySQLUsed) {
            this.database = new SQLite(this, "database.db");
            if (mySQLEnabled) {
                log.warn("No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                log.info("Using SQLite for storing data!");
            }
        }

        database.createTables();

        saver = new AsyncSaver();
        saver.start();
        Backup.loadDatabaseFromBackup();

        new JoinQuitListener();

        new QuestItemHandler();

        eventScheduling = new EventScheduling(SCHEDULE_TYPES);
        lastExecutionCache = new LastExecutionCache(getDataFolder());

        new GlobalObjectives();

        new CombatTagger();

        ConversationColors.loadColors();

        new MobKillListener();

        new CustomDropListener();

        new QuestCommand(adventure, new PlayerLogWatcher(receiverSelector), debugHistoryHandler);
        new JournalCommand();
        new BackpackCommand();
        new CancelQuestCommand();
        new CompassCommand();
        new LangCommand();

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
        registerConditions("language", LanguageCondition.class);
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
        registerConditions("burning", BurningCondition.class);
        registerConditions("inconversation", InConversationCondition.class);
        registerConditions("hunger", HungerCondition.class);

        registerEvents("objective", ObjectiveEvent.class);
        registerEvents("command", CommandEvent.class);
        registerEvent("tag", new TagPlayerEventFactory(this, getSaver()));
        registerEvent("globaltag", new TagGlobalEventFactory(this));
        registerEvent("journal", new JournalEventFactory(this, InstantSource.system(), getSaver()));
        registerNonStaticEvent("teleport", new TeleportEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("explosion", new ExplosionEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("lightning", new LightningEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("point", new PointEventFactory());
        registerEvent("globalpoint", new GlobalPointEventFactory());
        registerEvents("give", GiveEvent.class);
        registerEvents("take", TakeEvent.class);
        registerNonStaticEvent("conversation", new ConversationEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("kill", new KillEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("effect", new EffectEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("deleffect", new DeleteEffectEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("deletepoint", new DeletePointEventFactory());
        registerEvents("spawn", SpawnMobEvent.class);
        registerEvents("killmob", KillMobEvent.class);
        registerEvent("time", new TimeEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("weather", new WeatherEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvents("folder", FolderEvent.class);
        registerEvent("setblock", new SetBlockEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("damage", new DamageEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("party", new PartyEventFactory());
        registerEvents("clear", ClearEvent.class);
        registerEvents("run", RunEvent.class);
        registerNonStaticEvent("givejournal", new GiveJournalEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvents("sudo", SudoEvent.class);
        registerEvents("opsudo", OpSudoEvent.class);
        registerEvents("chestgive", ChestGiveEvent.class);
        registerEvent("chesttake", new ChestTakeEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("chestclear", new ChestClearEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvents("compass", CompassEvent.class);
        registerNonStaticEvent("cancel", new CancelEventFactory());
        registerNonStaticEvent("score", new ScoreboardEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("lever", new LeverEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("door", new DoorEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("if", new IfElseEventFactory());
        registerEvents("variable", VariableEvent.class);
        registerNonStaticEvent("language", new LanguageEventFactory(this));
        registerEvents("pickrandom", PickRandomEvent.class);
        registerNonStaticEvent("experience", new ExperienceEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("notify", new NotifyEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvent("notifyall", new NotifyAllEventFactory(getServer(), getServer().getScheduler(), this));
        registerEvents("chat", ChatEvent.class);
        registerEvents("freeze", FreezeEvent.class);
        registerNonStaticEvent("burn", new BurnEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("velocity", new VelocityEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("hunger", new HungerEventFactory(getServer(), getServer().getScheduler(), this));
        registerNonStaticEvent("cancelconversation", new CancelConversationEventFactory());
        registerEvent("deleteglobalpoint", new DeleteGlobalPointEventFactory());

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
            registerObjectives("equip", EquipItemObjective.class);
        }

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

        registerVariable("condition", ConditionVariable.class);
        registerVariable("tag", TagVariable.class);
        registerVariable("globaltag", GlobalTagVariable.class);
        registerVariable("player", PlayerNameVariable.class);
        registerVariable("npc", NpcNameVariable.class);
        registerVariable("objective", ObjectivePropertyVariable.class);
        registerVariable("point", PointVariable.class);
        registerVariable("globalpoint", GlobalPointVariable.class);
        registerVariable("item", ItemVariable.class);
        registerVariable("version", VersionVariable.class);
        registerVariable("location", LocationVariable.class);
        registerVariable("math", MathVariable.class);
        registerVariable("randomnumber", RandomNumberVariable.class);

        registerScheduleType("realtime-daily", RealtimeDailySchedule.class, new RealtimeDailyScheduler(lastExecutionCache));
        registerScheduleType("realtime-cron", RealtimeCronSchedule.class, new RealtimeCronScheduler(lastExecutionCache));

        new Compatibility();
        globalData = new GlobalData();

        // schedule quest data loading on the first tick, so all other
        // plugins can register their types
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            loadData();
            for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                final PlayerData playerData = new PlayerData(onlineProfile);
                playerDataMap.put(onlineProfile, playerData);
                playerData.startObjectives();
                playerData.getJournal().update();
                if (playerData.getConversation() != null) {
                    new ConversationResumer(onlineProfile, playerData.getConversation());
                }
            }

            try {
                playerHider = new PlayerHider();
            } catch (final InstructionParseException e) {
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

        final Map<String, InstructionMetricsSupplier<? extends ID>> metricsSuppliers = new HashMap<>();
        metricsSuppliers.put("conditions", new CompositeInstructionMetricsSupplier<>(CONDITIONS::keySet, CONDITION_TYPES::keySet));
        metricsSuppliers.put("events", new CompositeInstructionMetricsSupplier<>(EVENTS::keySet, eventTypes::keySet));
        metricsSuppliers.put("objectives", new CompositeInstructionMetricsSupplier<>(OBJECTIVES::keySet, OBJECTIVE_TYPES::keySet));
        metricsSuppliers.put("variables", new CompositeInstructionMetricsSupplier<>(VARIABLES::keySet, VARIABLE_TYPES::keySet));
        new BStatsMetrics(this, new Metrics(this, BSTATS_METRICS_ID), metricsSuppliers);

        setupUpdater();

        rpgMenu = new RPGMenu();
        rpgMenu.onEnable();

        PaperLib.suggestPaper(this);
        log.info("BetonQuest successfully enabled!");
    }

    private void setupUpdater() {
        final Version pluginVersion = new Version(this.getDescription().getVersion());
        final File updateFolder = getServer().getUpdateFolderFile();
        final File tempFile = new File(updateFolder, this.getFile().getName() + ".temp");
        final File finalFile = new File(updateFolder, this.getFile().getName());
        final UpdateDownloader updateDownloader = new UpdateDownloader(new File(".").toURI(), tempFile, finalFile);
        final GitHubReleaseSource gitHubReleaseSource = new GitHubReleaseSource("https://api.github.com/repos/BetonQuest/BetonQuest",
                new WebContentSource(GitHubReleaseSource.HTTP_CODE_HANDLER));
        final NexusReleaseAndDevelopmentSource nexusReleaseAndDevelopmentSource = new NexusReleaseAndDevelopmentSource("https://betonquest.org/nexus",
                new WebContentSource());
        final List<ReleaseUpdateSource> releaseHandlers = List.of(gitHubReleaseSource, nexusReleaseAndDevelopmentSource);
        final List<DevelopmentUpdateSource> developmentHandlers = List.of(nexusReleaseAndDevelopmentSource);
        final UpdateSourceHandler updateSourceHandler = new UpdateSourceHandler(releaseHandlers, developmentHandlers);
        updater = new Updater(config, pluginVersion, updateSourceHandler, updateDownloader, this,
                getServer().getScheduler(), InstantSource.system());
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
     * Loads events and conditions to the maps
     */
    @SuppressWarnings({"PMD.NcssCount", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    public void loadData() {
        eventScheduling.stopAll();

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
        CANCELERS.clear();

        loadQuestCanceler();

        // load new data
        for (final QuestPackage pack : Config.getPackages().values()) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            final ConfigurationSection eConfig = Config.getPackages().get(packName).getConfig().getConfigurationSection("events");
            if (eConfig != null) {
                for (final String key : eConfig.getKeys(false)) {
                    if (key.contains(" ")) {
                        log.warn(pack,
                                "Event name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                        continue;
                    }
                    final EventID identifier;
                    try {
                        identifier = new EventID(pack, key);
                    } catch (final ObjectNotFoundException e) {
                        log.warn(pack, "Error while loading event '" + packName + "." + key + "': " + e.getMessage(), e);
                        continue;
                    }
                    final String type;
                    try {
                        type = identifier.generateInstruction().getPart(0);
                    } catch (final InstructionParseException e) {
                        log.warn(pack, "Objective type not defined in '" + packName + "." + key + "'", e);
                        continue;
                    }
                    final QuestEventFactory eventFactory = getEventFactory(type);
                    if (eventFactory == null) {
                        // if it's null then there is no such type registered, log an error
                        log.warn(pack, "Event type " + type + " is not registered, check if it's"
                                + " spelled correctly in '" + identifier + "' event.");
                        continue;
                    }

                    try {
                        final QuestEvent event = eventFactory.parseEventInstruction(identifier.generateInstruction());
                        EVENTS.put(identifier, event);
                        log.debug(pack, "  Event '" + identifier + "' loaded");
                    } catch (final InstructionParseException e) {
                        log.warn(pack, "Error in '" + identifier + "' event (" + type + "): " + e.getMessage(), e);
                    }
                }
            }
            final ConfigurationSection cConfig = pack.getConfig().getConfigurationSection("conditions");
            if (cConfig != null) {
                for (final String key : cConfig.getKeys(false)) {
                    if (key.contains(" ")) {
                        log.warn(pack,
                                "Condition name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                        continue;
                    }
                    final ConditionID identifier;
                    try {
                        identifier = new ConditionID(pack, key);
                    } catch (final ObjectNotFoundException e) {
                        log.warn(pack, "Error while loading condition '" + packName + "." + key + "': " + e.getMessage(), e);
                        continue;
                    }
                    final String type;
                    try {
                        type = identifier.generateInstruction().getPart(0);
                    } catch (final InstructionParseException e) {
                        log.warn(pack, "Condition type not defined in '" + packName + "." + key + "'", e);
                        continue;
                    }
                    final Class<? extends Condition> conditionClass = CONDITION_TYPES.get(type);
                    // if it's null then there is no such type registered, log an
                    // error
                    if (conditionClass == null) {
                        log.warn(pack, "Condition type " + type + " is not registered,"
                                + " check if it's spelled correctly in '" + identifier + "' condition.");
                        continue;
                    }
                    try {
                        final Condition condition = conditionClass.getConstructor(Instruction.class)
                                .newInstance(identifier.generateInstruction());
                        CONDITIONS.put(identifier, condition);
                        log.debug(pack, "  Condition '" + identifier + "' loaded");
                    } catch (final InvocationTargetException e) {
                        if (e.getCause() instanceof InstructionParseException) {
                            log.warn(pack, "Error in '" + identifier + "' condition (" + type + "): " + e.getCause().getMessage(), e);
                        } else {
                            log.reportException(pack, e);
                        }
                    } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                        log.reportException(pack, e);
                    }
                }
            }
            final ConfigurationSection oConfig = pack.getConfig().getConfigurationSection("objectives");
            if (oConfig != null) {
                for (final String key : oConfig.getKeys(false)) {
                    if (key.contains(" ")) {
                        log.warn(pack,
                                "Objective name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                        continue;
                    }
                    final ObjectiveID identifier;
                    try {
                        identifier = new ObjectiveID(pack, key);
                    } catch (final ObjectNotFoundException e) {
                        log.warn(pack, "Error while loading objective '" + packName + "." + key + "': " + e.getMessage(), e);
                        continue;
                    }
                    final String type;
                    try {
                        type = identifier.generateInstruction().getPart(0);
                    } catch (final InstructionParseException e) {
                        log.warn(pack, "Objective type not defined in '" + packName + "." + key + "'", e);
                        continue;
                    }
                    final Class<? extends Objective> objectiveClass = OBJECTIVE_TYPES.get(type);
                    // if it's null then there is no such type registered, log an
                    // error
                    if (objectiveClass == null) {
                        log.warn(pack,
                                "Objective type " + type + " is not registered, check if it's"
                                        + " spelled correctly in '" + identifier + "' objective.");
                        continue;
                    }
                    try {
                        final Objective objective = objectiveClass.getConstructor(Instruction.class)
                                .newInstance(identifier.generateInstruction());
                        OBJECTIVES.put(identifier, objective);
                        log.debug(pack, "  Objective '" + identifier + "' loaded");
                    } catch (final InvocationTargetException e) {
                        if (e.getCause() instanceof InstructionParseException) {
                            log.warn(pack, "Error in '" + identifier + "' objective (" + type + "): " + e.getCause().getMessage(), e);
                        } else {
                            log.reportException(pack, e);
                        }
                    } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                        log.reportException(pack, e);
                    }
                }
            }
            final ConfigurationSection conversationsConfig = pack.getConfig().getConfigurationSection("conversations");
            if (conversationsConfig != null) {
                for (final String convName : conversationsConfig.getKeys(false)) {
                    try {
                        CONVERSATIONS.put(pack.getQuestPath() + "." + convName, new ConversationData(pack, convName, conversationsConfig.getConfigurationSection(convName)));
                    } catch (final InstructionParseException e) {
                        log.warn(pack, "Error in '" + packName + "." + convName + "' conversation: " + e.getMessage(), e);
                    }
                }
            }
            // load schedules
            eventScheduling.loadData(pack);
            // check external pointers
            ConversationData.postEnableCheck();
            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        log.info("There are " + CONDITIONS.size() + " conditions, " + EVENTS.size() + " events, "
                + OBJECTIVES.size() + " objectives and " + CONVERSATIONS.size() + " conversations loaded from "
                + Config.getPackages().size() + " packages.");
        // start those freshly loaded objectives for all players
        for (final PlayerData playerData : playerDataMap.values()) {
            playerData.startObjectives();
        }
        //start all schedules
        eventScheduling.startAll();

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
        Config.setup(this);
        Notify.load();
        lastExecutionCache.reload();

        // reload updater settings
        BetonQuest.getInstance().getUpdater().search();
        // stop current global locations listener
        // and start new one with reloaded configs
        log.debug("Restarting global locations");
        new GlobalObjectives();
        ConversationColors.loadColors();
        Compatibility.reload();
        // load all events, conditions, objectives, conversations etc.
        loadData();
        // start objectives and update journals for every online profiles
        for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            log.debug("Updating journal for player " + onlineProfile);
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
            log.error("Could not start PlayerHider! " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    @Override
    public void onDisable() {
        //stop all schedules
        if (eventScheduling != null) {
            eventScheduling.stopAll();
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
        log.info("BetonQuest succesfully disabled!");

        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        if (rpgMenu != null) {
            rpgMenu.onDisable();
        }
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
     * Returns the schedules cache instance.
     *
     * @return LastExecutionCache instance
     */
    public LastExecutionCache getLastExecutionCache() {
        return lastExecutionCache;
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
     * getPlayerData(Profile profile).
     *
     * @param profile    the {@link Profile} of the player
     * @param playerData PlayerData object to store
     */
    public void putPlayerData(final Profile profile, final PlayerData playerData) {
        log.debug("Inserting data for " + profile);
        playerDataMap.put(profile, playerData);
    }

    /**
     * Retrieves PlayerData object for specified profile. If the playerData does
     * not exist but the profile is online, it will create new playerData on the
     * main thread and put it into the map.
     *
     * @param profile the {@link Profile} of the player
     * @return PlayerData object for the player
     */
    public PlayerData getPlayerData(final Profile profile) {
        PlayerData playerData = playerDataMap.get(profile);
        if (playerData == null && profile.getOnlineProfile().isPresent()) {
            playerData = new PlayerData(profile);
            putPlayerData(profile, playerData);
        }
        return playerData;
    }

    public PlayerData getOfflinePlayerData(final Profile profile) {
        final PlayerData playerData = getPlayerData(profile);
        if (playerData == null) {
            return new PlayerData(profile);
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
     * @param profile the {@link Profile} of the player whose playerData is to be removed
     */
    public void removePlayerData(final Profile profile) {
        playerDataMap.remove(profile);
    }

    /**
     * Registers new condition classes by their names
     *
     * @param name           name of the condition type
     * @param conditionClass class object for the condition
     */
    public void registerConditions(final String name, final Class<? extends Condition> conditionClass) {
        log.debug("Registering " + name + " condition type");
        CONDITION_TYPES.put(name, conditionClass);
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
        log.debug("Registering " + name + " event type");
        eventTypes.put(name, new FromClassQuestEventFactory<>(eventClass));
    }

    /**
     * Registers an event that does not support static execution with its name
     * and a factory to create new normal instances of the event.
     *
     * @param name         name of the event
     * @param eventFactory factory to create the event
     */
    public void registerNonStaticEvent(final String name, final EventFactory eventFactory) {
        registerEvent(name, eventFactory, new NullStaticEventFactory());
    }

    /**
     * Registers an event with its name and a single factory to create both normal and
     * static instances of the event.
     *
     * @param name         name of the event
     * @param eventFactory factory to create the event and the static event
     * @param <T>          type of factory that creates both normal and static instances of the event.
     */
    public <T extends EventFactory & StaticEventFactory> void registerEvent(final String name, final T eventFactory) {
        registerEvent(name, eventFactory, eventFactory);
    }

    /**
     * Registers an event with its name and two factories to create normal and
     * static instances of the event.
     *
     * @param name               name of the event
     * @param eventFactory       factory to create the event
     * @param staticEventFactory factory to create the static event
     */
    public void registerEvent(final String name, final EventFactory eventFactory, final StaticEventFactory staticEventFactory) {
        log.debug("Registering " + name + " event type");
        eventTypes.put(name, new QuestEventFactoryAdapter(eventFactory, staticEventFactory));
    }

    /**
     * Registers new objective classes by their names
     *
     * @param name           name of the objective type
     * @param objectiveClass class object for the objective
     */
    public void registerObjectives(final String name, final Class<? extends Objective> objectiveClass) {
        log.debug("Registering " + name + " objective type");
        OBJECTIVE_TYPES.put(name, objectiveClass);
    }

    /**
     * Registers new conversation input/output class.
     *
     * @param name        name of the IO type
     * @param convIOClass class object to register
     */
    public void registerConversationIO(final String name, final Class<? extends ConversationIO> convIOClass) {
        log.debug("Registering " + name + " conversation IO type");
        CONVERSATION_IO_TYPES.put(name, convIOClass);
    }

    /**
     * Registers new interceptor class.
     *
     * @param name             name of the interceptor type
     * @param interceptorClass class object to register
     */
    public void registerInterceptor(final String name, final Class<? extends Interceptor> interceptorClass) {
        log.debug("Registering " + name + " interceptor type");
        INTERCEPTOR_TYPES.put(name, interceptorClass);
    }

    /**
     * Registers new notify input/output class.
     *
     * @param name    name of the IO type
     * @param ioClass class object to register
     */
    public void registerNotifyIO(final String name, final Class<? extends NotifyIO> ioClass) {
        log.debug("Registering " + name + " notify IO type");
        NOTIFY_IO_TYPES.put(name, ioClass);
    }

    /**
     * Registers new variable type.
     *
     * @param name     name of the variable type
     * @param variable class object of this type
     */
    public void registerVariable(final String name, final Class<? extends Variable> variable) {
        log.debug("Registering " + name + " variable type");
        VARIABLE_TYPES.put(name, variable);
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
     * Returns the list of objectives of this player
     *
     * @param profile the {@link Profile} of the player
     * @return list of this player's active objectives
     */
    public List<Objective> getPlayerObjectives(final Profile profile) {
        final List<Objective> list = new ArrayList<>();
        for (final Objective objective : OBJECTIVES.values()) {
            if (objective.containsPlayer(profile)) {
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
     * @param profile  the {@link Profile} of the player
     * @return the value of this variable for given player
     */
    public String getVariableValue(final String packName, final String name, final Profile profile) {
        if (!Config.getPackages().containsKey(packName)) {
            log.warn("Variable '" + name + "' contains the non-existent package '" + packName + "' !");
            return "";
        }
        final QuestPackage pack = Config.getPackages().get(packName);
        try {
            final Variable var = createVariable(pack, name);
            if (var == null) {
                log.warn(pack, "Could not resolve variable '" + name + "'.");
                return "";
            }
            if (profile == null && !var.isStaticness()) {
                log.warn(pack, "Variable '" + name + "' cannot be executed without a profile reference!");
                return "";
            }
            return var.getValue(profile);
        } catch (final InstructionParseException e) {
            log.warn(pack, "&cCould not create variable '" + name + "': " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Fetches the factory to create the event registered with the given name.
     *
     * @param name the name of the event
     * @return a factory to create the event
     */
    public QuestEventFactory getEventFactory(final String name) {
        return eventTypes.get(name);
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

    /**
     * @return the condition types map
     */
    public Map<String, Class<? extends Condition>> getConditionTypes() {
        return new HashMap<>(CONDITION_TYPES);
    }

    /**
     * @return the objective types map
     */
    public Map<String, Class<? extends Objective>> getObjectiveTypes() {
        return new HashMap<>(OBJECTIVE_TYPES);
    }
}
