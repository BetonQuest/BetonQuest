package org.betonquest.betonquest.quest.registry;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
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
import org.betonquest.betonquest.conditions.ItemDurabilityCondition;
import org.betonquest.betonquest.conditions.JournalCondition;
import org.betonquest.betonquest.conditions.LanguageCondition;
import org.betonquest.betonquest.conditions.LocationCondition;
import org.betonquest.betonquest.conditions.LookingAtCondition;
import org.betonquest.betonquest.conditions.MooncycleCondition;
import org.betonquest.betonquest.conditions.NumberCompareCondition;
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
import org.betonquest.betonquest.conditions.StageCondition;
import org.betonquest.betonquest.conditions.TagCondition;
import org.betonquest.betonquest.conditions.TestForBlockCondition;
import org.betonquest.betonquest.conditions.TimeCondition;
import org.betonquest.betonquest.conditions.VariableCondition;
import org.betonquest.betonquest.conditions.WeatherCondition;
import org.betonquest.betonquest.conditions.WorldCondition;
import org.betonquest.betonquest.events.FolderEvent;
import org.betonquest.betonquest.events.ObjectiveEvent;
import org.betonquest.betonquest.events.RunEvent;
import org.betonquest.betonquest.events.SpawnMobEvent;
import org.betonquest.betonquest.events.TakeEvent;
import org.betonquest.betonquest.events.VariableEvent;
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
import org.betonquest.betonquest.objectives.ResourcePackObjective;
import org.betonquest.betonquest.objectives.RideObjective;
import org.betonquest.betonquest.objectives.ShearObjective;
import org.betonquest.betonquest.objectives.SmeltingObjective;
import org.betonquest.betonquest.objectives.StageObjective;
import org.betonquest.betonquest.objectives.StepObjective;
import org.betonquest.betonquest.objectives.TameObjective;
import org.betonquest.betonquest.objectives.VariableObjective;
import org.betonquest.betonquest.quest.event.burn.BurnEventFactory;
import org.betonquest.betonquest.quest.event.cancel.CancelEventFactory;
import org.betonquest.betonquest.quest.event.chat.ChatEventFactory;
import org.betonquest.betonquest.quest.event.chest.ChestClearEventFactory;
import org.betonquest.betonquest.quest.event.chest.ChestGiveEventFactory;
import org.betonquest.betonquest.quest.event.chest.ChestTakeEventFactory;
import org.betonquest.betonquest.quest.event.command.CommandEventFactory;
import org.betonquest.betonquest.quest.event.command.OpSudoEventFactory;
import org.betonquest.betonquest.quest.event.command.SudoEventFactory;
import org.betonquest.betonquest.quest.event.compass.CompassEventFactory;
import org.betonquest.betonquest.quest.event.conversation.CancelConversationEventFactory;
import org.betonquest.betonquest.quest.event.conversation.ConversationEventFactory;
import org.betonquest.betonquest.quest.event.damage.DamageEventFactory;
import org.betonquest.betonquest.quest.event.door.DoorEventFactory;
import org.betonquest.betonquest.quest.event.drop.DropEventFactory;
import org.betonquest.betonquest.quest.event.effect.DeleteEffectEventFactory;
import org.betonquest.betonquest.quest.event.effect.EffectEventFactory;
import org.betonquest.betonquest.quest.event.entity.RemoveEntityEventFactory;
import org.betonquest.betonquest.quest.event.experience.ExperienceEventFactory;
import org.betonquest.betonquest.quest.event.explosion.ExplosionEventFactory;
import org.betonquest.betonquest.quest.event.give.GiveEventFactory;
import org.betonquest.betonquest.quest.event.hunger.HungerEventFactory;
import org.betonquest.betonquest.quest.event.item.ItemDurabilityEventFactory;
import org.betonquest.betonquest.quest.event.journal.GiveJournalEventFactory;
import org.betonquest.betonquest.quest.event.journal.JournalEventFactory;
import org.betonquest.betonquest.quest.event.kill.KillEventFactory;
import org.betonquest.betonquest.quest.event.language.LanguageEventFactory;
import org.betonquest.betonquest.quest.event.lever.LeverEventFactory;
import org.betonquest.betonquest.quest.event.lightning.LightningEventFactory;
import org.betonquest.betonquest.quest.event.log.LogEventFactory;
import org.betonquest.betonquest.quest.event.logic.FirstEventFactory;
import org.betonquest.betonquest.quest.event.logic.IfElseEventFactory;
import org.betonquest.betonquest.quest.event.notify.NotifyAllEventFactory;
import org.betonquest.betonquest.quest.event.notify.NotifyEventFactory;
import org.betonquest.betonquest.quest.event.party.PartyEventFactory;
import org.betonquest.betonquest.quest.event.point.DeleteGlobalPointEventFactory;
import org.betonquest.betonquest.quest.event.point.DeletePointEventFactory;
import org.betonquest.betonquest.quest.event.point.GlobalPointEventFactory;
import org.betonquest.betonquest.quest.event.point.PointEventFactory;
import org.betonquest.betonquest.quest.event.random.PickRandomEventFactory;
import org.betonquest.betonquest.quest.event.run.RunForAllEventFactory;
import org.betonquest.betonquest.quest.event.run.RunIndependentEventFactory;
import org.betonquest.betonquest.quest.event.scoreboard.ScoreboardEventFactory;
import org.betonquest.betonquest.quest.event.setblock.SetBlockEventFactory;
import org.betonquest.betonquest.quest.event.stage.StageEventFactory;
import org.betonquest.betonquest.quest.event.tag.TagGlobalEventFactory;
import org.betonquest.betonquest.quest.event.tag.TagPlayerEventFactory;
import org.betonquest.betonquest.quest.event.teleport.TeleportEventFactory;
import org.betonquest.betonquest.quest.event.time.TimeEventFactory;
import org.betonquest.betonquest.quest.event.velocity.VelocityEventFactory;
import org.betonquest.betonquest.quest.event.weather.WeatherEventFactory;
import org.betonquest.betonquest.variables.ConditionVariable;
import org.betonquest.betonquest.variables.GlobalPointVariable;
import org.betonquest.betonquest.variables.GlobalTagVariable;
import org.betonquest.betonquest.variables.ItemDurabilityVariable;
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
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.InstantSource;

/**
 * Registers the Conditions, Events, Objectives and Variables that come with BetonQuest.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class CoreQuestTypes {
    /**
     * Logger Factory to create new custom Logger from.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Server used for primary server thread access.
     */
    private final Server server;

    /**
     * Scheduler used for primary server thread access.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin used for primary server thread access, type registration and general usage.
     */
    private final BetonQuest plugin;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory used in event factories
     * @param server        the server used for primary server thread access.
     * @param scheduler     used in event factories
     * @param plugin        used in event factories and for objective type registration
     */
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Registers the Quest Types.
     */
    public void register() {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions();
        registerEvents();
        registerObjectives();
        registerVariables();
    }

    private void registerConditions() {
        plugin.registerConditions("advancement", AdvancementCondition.class);
        plugin.registerConditions("and", ConjunctionCondition.class);
        plugin.registerConditions("armor", ArmorCondition.class);
        plugin.registerConditions("biome", BiomeCondition.class);
        plugin.registerConditions("burning", BurningCondition.class);
        plugin.registerConditions("check", CheckCondition.class);
        plugin.registerConditions("chestitem", ChestItemCondition.class);
        plugin.registerConditions("conversation", ConversationCondition.class);
        plugin.registerConditions("dayofweek", DayOfWeekCondition.class);
        plugin.registerConditions("effect", EffectCondition.class);
        plugin.registerConditions("empty", EmptySlotsCondition.class);
        plugin.registerConditions("entities", EntityCondition.class);
        plugin.registerConditions("experience", ExperienceCondition.class);
        plugin.registerConditions("facing", FacingCondition.class);
        plugin.registerConditions("fly", FlyingCondition.class);
        plugin.registerConditions("gamemode", GameModeCondition.class);
        plugin.registerConditions("globalpoint", GlobalPointCondition.class);
        plugin.registerConditions("globaltag", GlobalTagCondition.class);
        plugin.registerConditions("hand", HandCondition.class);
        plugin.registerConditions("health", HealthCondition.class);
        plugin.registerConditions("height", HeightCondition.class);
        plugin.registerConditions("hunger", HungerCondition.class);
        plugin.registerConditions("inconversation", InConversationCondition.class);
        plugin.registerConditions("item", ItemCondition.class);
        plugin.registerConditions("itemdurability", ItemDurabilityCondition.class);
        plugin.registerConditions("journal", JournalCondition.class);
        plugin.registerConditions("language", LanguageCondition.class);
        plugin.registerConditions("location", LocationCondition.class);
        plugin.registerConditions("looking", LookingAtCondition.class);
        plugin.registerConditions("mooncycle", MooncycleCondition.class);
        plugin.registerConditions("numbercompare", NumberCompareCondition.class);
        plugin.registerConditions("objective", ObjectiveCondition.class);
        plugin.registerConditions("or", AlternativeCondition.class);
        plugin.registerConditions("partialdate", PartialDateCondition.class);
        plugin.registerConditions("party", PartyCondition.class);
        plugin.registerConditions("permission", PermissionCondition.class);
        plugin.registerConditions("point", PointCondition.class);
        plugin.registerConditions("random", RandomCondition.class);
        plugin.registerConditions("rating", ArmorRatingCondition.class);
        plugin.registerConditions("realtime", RealTimeCondition.class);
        plugin.registerConditions("ride", RideCondition.class);
        plugin.registerConditions("score", ScoreboardCondition.class);
        plugin.registerConditions("sneak", SneakCondition.class);
        plugin.registerConditions("stage", StageCondition.class);
        plugin.registerConditions("tag", TagCondition.class);
        plugin.registerConditions("testforblock", TestForBlockCondition.class);
        plugin.registerConditions("time", TimeCondition.class);
        plugin.registerConditions("variable", VariableCondition.class);
        plugin.registerConditions("weather", WeatherCondition.class);
        plugin.registerConditions("world", WorldCondition.class);
    }

    private void registerEvents() {
        plugin.registerNonStaticEvent("burn", new BurnEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("cancel", new CancelEventFactory(loggerFactory));
        plugin.registerNonStaticEvent("cancelconversation", new CancelConversationEventFactory(loggerFactory));
        plugin.registerNonStaticEvent("chat", new ChatEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("chestclear", new ChestClearEventFactory(server, scheduler, plugin));
        plugin.registerEvent("chestgive", new ChestGiveEventFactory(server, scheduler, plugin));
        plugin.registerEvent("chesttake", new ChestTakeEventFactory(server, scheduler, plugin));
        plugin.registerNonStaticEvent("compass", new CompassEventFactory(loggerFactory, plugin, server.getPluginManager(), server, scheduler));
        plugin.registerEvent("command", new CommandEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("conversation", new ConversationEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("damage", new DamageEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("deleffect", new DeleteEffectEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("deleteglobalpoint", new DeleteGlobalPointEventFactory());
        plugin.registerEvent("deletepoint", new DeletePointEventFactory());
        plugin.registerEvent("door", new DoorEventFactory(server, scheduler, plugin));
        plugin.registerEvent("drop", new DropEventFactory(server, scheduler, plugin));
        plugin.registerNonStaticEvent("effect", new EffectEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("experience", new ExperienceEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("explosion", new ExplosionEventFactory(server, scheduler, plugin));
        plugin.registerEvents("folder", FolderEvent.class);
        plugin.registerEvent("first", new FirstEventFactory());
        plugin.registerNonStaticEvent("give", new GiveEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("givejournal", new GiveJournalEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("globaltag", new TagGlobalEventFactory(plugin));
        plugin.registerEvent("globalpoint", new GlobalPointEventFactory());
        plugin.registerNonStaticEvent("hunger", new HungerEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("if", new IfElseEventFactory());
        plugin.registerNonStaticEvent("itemdurability", new ItemDurabilityEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("journal", new JournalEventFactory(loggerFactory, plugin, InstantSource.system(), plugin.getSaver()));
        plugin.registerNonStaticEvent("kill", new KillEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("language", new LanguageEventFactory(plugin));
        plugin.registerEvent("lever", new LeverEventFactory(server, scheduler, plugin));
        plugin.registerEvent("lightning", new LightningEventFactory(server, scheduler, plugin));
        plugin.registerEvent("log", new LogEventFactory(loggerFactory));
        plugin.registerNonStaticEvent("notify", new NotifyEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("notifyall", new NotifyAllEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvents("objective", ObjectiveEvent.class);
        plugin.registerNonStaticEvent("opsudo", new OpSudoEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("party", new PartyEventFactory(loggerFactory));
        plugin.registerEvent("pickrandom", new PickRandomEventFactory());
        plugin.registerNonStaticEvent("point", new PointEventFactory(loggerFactory));
        plugin.registerEvent("removeentity", new RemoveEntityEventFactory(server, scheduler, plugin));
        plugin.registerEvents("run", RunEvent.class);
        plugin.registerEvent("runForAll", new RunForAllEventFactory());
        plugin.registerEvent("runIndependent", new RunIndependentEventFactory());
        plugin.registerEvent("setblock", new SetBlockEventFactory(server, scheduler, plugin));
        plugin.registerNonStaticEvent("score", new ScoreboardEventFactory(server, scheduler, plugin));
        plugin.registerEvents("spawn", SpawnMobEvent.class);
        plugin.registerNonStaticEvent("stage", new StageEventFactory(plugin));
        plugin.registerNonStaticEvent("sudo", new SudoEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("tag", new TagPlayerEventFactory(plugin, plugin.getSaver()));
        plugin.registerEvents("take", TakeEvent.class);
        plugin.registerNonStaticEvent("teleport", new TeleportEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerEvent("time", new TimeEventFactory(server, scheduler, plugin));
        plugin.registerEvents("variable", VariableEvent.class);
        plugin.registerNonStaticEvent("velocity", new VelocityEventFactory(loggerFactory, server, scheduler, plugin));
        plugin.registerNonStaticEvent("weather", new WeatherEventFactory(loggerFactory, server, scheduler, plugin));
    }

    private void registerObjectives() {
        plugin.registerObjectives("action", ActionObjective.class);
        plugin.registerObjectives("arrow", ArrowShootObjective.class);
        plugin.registerObjectives("block", BlockObjective.class);
        plugin.registerObjectives("breed", BreedObjective.class);
        plugin.registerObjectives("brew", BrewObjective.class);
        plugin.registerObjectives("chestput", ChestPutObjective.class);
        plugin.registerObjectives("command", CommandObjective.class);
        plugin.registerObjectives("consume", ConsumeObjective.class);
        plugin.registerObjectives("craft", CraftingObjective.class);
        plugin.registerObjectives("delay", DelayObjective.class);
        plugin.registerObjectives("die", DieObjective.class);
        plugin.registerObjectives("enchant", EnchantObjective.class);
        plugin.registerObjectives("experience", ExperienceObjective.class);
        plugin.registerObjectives("fish", FishObjective.class);
        plugin.registerObjectives("interact", EntityInteractObjective.class);
        plugin.registerObjectives("kill", KillPlayerObjective.class);
        plugin.registerObjectives("location", LocationObjective.class);
        plugin.registerObjectives("login", LoginObjective.class);
        plugin.registerObjectives("logout", LogoutObjective.class);
        plugin.registerObjectives("mobkill", MobKillObjective.class);
        plugin.registerObjectives("password", PasswordObjective.class);
        plugin.registerObjectives("pickup", PickupObjective.class);
        plugin.registerObjectives("ride", RideObjective.class);
        plugin.registerObjectives("shear", ShearObjective.class);
        plugin.registerObjectives("smelt", SmeltingObjective.class);
        plugin.registerObjectives("stage", StageObjective.class);
        plugin.registerObjectives("step", StepObjective.class);
        plugin.registerObjectives("tame", TameObjective.class);
        plugin.registerObjectives("variable", VariableObjective.class);
        if (PaperLib.isPaper()) {
            plugin.registerObjectives("equip", EquipItemObjective.class);
            plugin.registerObjectives("jump", JumpObjective.class);
            plugin.registerObjectives("resourcepack", ResourcePackObjective.class);
        }
    }

    private void registerVariables() {
        plugin.registerVariable("condition", ConditionVariable.class);
        plugin.registerVariable("globalpoint", GlobalPointVariable.class);
        plugin.registerVariable("globaltag", GlobalTagVariable.class);
        plugin.registerVariable("item", ItemVariable.class);
        plugin.registerVariable("itemdurability", ItemDurabilityVariable.class);
        plugin.registerVariable("location", LocationVariable.class);
        plugin.registerVariable("math", MathVariable.class);
        plugin.registerVariable("npc", NpcNameVariable.class);
        plugin.registerVariable("objective", ObjectivePropertyVariable.class);
        plugin.registerVariable("point", PointVariable.class);
        plugin.registerVariable("player", PlayerNameVariable.class);
        plugin.registerVariable("randomnumber", RandomNumberVariable.class);
        plugin.registerVariable("tag", TagVariable.class);
        plugin.registerVariable("version", VersionVariable.class);
    }
}
