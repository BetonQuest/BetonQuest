package org.betonquest.betonquest.quest.registry;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.conditions.AlternativeCondition;
import org.betonquest.betonquest.conditions.ArmorCondition;
import org.betonquest.betonquest.conditions.ArmorRatingCondition;
import org.betonquest.betonquest.conditions.BiomeCondition;
import org.betonquest.betonquest.conditions.BurningCondition;
import org.betonquest.betonquest.conditions.CheckCondition;
import org.betonquest.betonquest.conditions.ChestItemCondition;
import org.betonquest.betonquest.conditions.ConjunctionCondition;
import org.betonquest.betonquest.conditions.ConversationCondition;
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
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.advancement.AdvancementConditionFactory;
import org.betonquest.betonquest.quest.condition.block.BlockConditionFactory;
import org.betonquest.betonquest.quest.condition.realtime.DayOfWeekConditionFactory;
import org.betonquest.betonquest.quest.condition.realtime.PartialDateConditionFactory;
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
import org.betonquest.betonquest.quest.registry.type.ConditionTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.VariableTypeRegistry;
import org.betonquest.betonquest.quest.variable.condition.ConditionVariableFactory;
import org.betonquest.betonquest.quest.variable.eval.EvalVariableFactory;
import org.betonquest.betonquest.quest.variable.name.NpcNameVariableFactory;
import org.betonquest.betonquest.quest.variable.name.PlayerNameVariableFactory;
import org.betonquest.betonquest.variables.GlobalPointVariable;
import org.betonquest.betonquest.variables.GlobalTagVariable;
import org.betonquest.betonquest.variables.ItemDurabilityVariable;
import org.betonquest.betonquest.variables.ItemVariable;
import org.betonquest.betonquest.variables.LocationVariable;
import org.betonquest.betonquest.variables.MathVariable;
import org.betonquest.betonquest.variables.ObjectivePropertyVariable;
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
    private final BetonQuest betonQuest;

    /**
     * Server, Scheduler and Plugin used for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory used in event factories
     * @param server        the server used for primary server thread access.
     * @param scheduler     the scheduler used for primary server thread access
     * @param betonQuest    the plugin used for primary server access and type registration
     */
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest betonQuest) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.betonQuest = betonQuest;
        this.data = new PrimaryServerThreadData(server, scheduler, betonQuest);
    }

    /**
     * Registers the Quest Types.
     *
     * @param questTypeRegistries the registry to register the types in
     */
    public void register(final QuestTypeRegistries questTypeRegistries) {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions(questTypeRegistries.getConditionTypes());
        registerEvents();
        registerObjectives();
        registerVariables(questTypeRegistries.getVariableTypes());
    }

    private void registerConditions(final ConditionTypeRegistry conditionTypes) {
        conditionTypes.register("advancement", new AdvancementConditionFactory(data, loggerFactory));
        betonQuest.registerConditions("and", ConjunctionCondition.class);
        betonQuest.registerConditions("armor", ArmorCondition.class);
        betonQuest.registerConditions("biome", BiomeCondition.class);
        betonQuest.registerConditions("burning", BurningCondition.class);
        betonQuest.registerConditions("check", CheckCondition.class);
        betonQuest.registerConditions("chestitem", ChestItemCondition.class);
        betonQuest.registerConditions("conversation", ConversationCondition.class);
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        betonQuest.registerConditions("effect", EffectCondition.class);
        betonQuest.registerConditions("empty", EmptySlotsCondition.class);
        betonQuest.registerConditions("entities", EntityCondition.class);
        betonQuest.registerConditions("experience", ExperienceCondition.class);
        betonQuest.registerConditions("facing", FacingCondition.class);
        betonQuest.registerConditions("fly", FlyingCondition.class);
        betonQuest.registerConditions("gamemode", GameModeCondition.class);
        betonQuest.registerConditions("globalpoint", GlobalPointCondition.class);
        betonQuest.registerConditions("globaltag", GlobalTagCondition.class);
        betonQuest.registerConditions("hand", HandCondition.class);
        betonQuest.registerConditions("health", HealthCondition.class);
        betonQuest.registerConditions("height", HeightCondition.class);
        betonQuest.registerConditions("hunger", HungerCondition.class);
        betonQuest.registerConditions("inconversation", InConversationCondition.class);
        betonQuest.registerConditions("item", ItemCondition.class);
        betonQuest.registerConditions("itemdurability", ItemDurabilityCondition.class);
        betonQuest.registerConditions("journal", JournalCondition.class);
        betonQuest.registerConditions("language", LanguageCondition.class);
        betonQuest.registerConditions("location", LocationCondition.class);
        betonQuest.registerConditions("looking", LookingAtCondition.class);
        betonQuest.registerConditions("mooncycle", MooncycleCondition.class);
        betonQuest.registerConditions("numbercompare", NumberCompareCondition.class);
        betonQuest.registerConditions("objective", ObjectiveCondition.class);
        betonQuest.registerConditions("or", AlternativeCondition.class);
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        betonQuest.registerConditions("party", PartyCondition.class);
        betonQuest.registerConditions("permission", PermissionCondition.class);
        betonQuest.registerConditions("point", PointCondition.class);
        betonQuest.registerConditions("random", RandomCondition.class);
        betonQuest.registerConditions("rating", ArmorRatingCondition.class);
        betonQuest.registerConditions("realtime", RealTimeCondition.class);
        betonQuest.registerConditions("ride", RideCondition.class);
        betonQuest.registerConditions("score", ScoreboardCondition.class);
        betonQuest.registerConditions("sneak", SneakCondition.class);
        betonQuest.registerConditions("stage", StageCondition.class);
        betonQuest.registerConditions("tag", TagCondition.class);
        conditionTypes.register("testforblock", new BlockConditionFactory(data));
        betonQuest.registerConditions("time", TimeCondition.class);
        betonQuest.registerConditions("variable", VariableCondition.class);
        betonQuest.registerConditions("weather", WeatherCondition.class);
        betonQuest.registerConditions("world", WorldCondition.class);
    }

    private void registerEvents() {
        betonQuest.registerNonStaticEvent("burn", new BurnEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("cancel", new CancelEventFactory(loggerFactory));
        betonQuest.registerNonStaticEvent("cancelconversation", new CancelConversationEventFactory(loggerFactory));
        betonQuest.registerNonStaticEvent("chat", new ChatEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("chestclear", new ChestClearEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvent("chestgive", new ChestGiveEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvent("chesttake", new ChestTakeEventFactory(server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("compass", new CompassEventFactory(loggerFactory, betonQuest, server.getPluginManager(), server, scheduler));
        betonQuest.registerEvent("command", new CommandEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("conversation", new ConversationEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("damage", new DamageEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("deleffect", new DeleteEffectEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("deleteglobalpoint", new DeleteGlobalPointEventFactory());
        betonQuest.registerEvent("deletepoint", new DeletePointEventFactory());
        betonQuest.registerEvent("door", new DoorEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvent("drop", new DropEventFactory(server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("effect", new EffectEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("experience", new ExperienceEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("explosion", new ExplosionEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvents("folder", FolderEvent.class);
        betonQuest.registerEvent("first", new FirstEventFactory());
        betonQuest.registerNonStaticEvent("give", new GiveEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("givejournal", new GiveJournalEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("globaltag", new TagGlobalEventFactory(betonQuest));
        betonQuest.registerEvent("globalpoint", new GlobalPointEventFactory());
        betonQuest.registerNonStaticEvent("hunger", new HungerEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("if", new IfElseEventFactory());
        betonQuest.registerNonStaticEvent("itemdurability", new ItemDurabilityEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("journal", new JournalEventFactory(loggerFactory, betonQuest, InstantSource.system(), betonQuest.getSaver()));
        betonQuest.registerNonStaticEvent("kill", new KillEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("language", new LanguageEventFactory(betonQuest));
        betonQuest.registerEvent("lever", new LeverEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvent("lightning", new LightningEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvent("log", new LogEventFactory(loggerFactory));
        betonQuest.registerNonStaticEvent("notify", new NotifyEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("notifyall", new NotifyAllEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvents("objective", ObjectiveEvent.class);
        betonQuest.registerNonStaticEvent("opsudo", new OpSudoEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("party", new PartyEventFactory(loggerFactory));
        betonQuest.registerEvent("pickrandom", new PickRandomEventFactory());
        betonQuest.registerNonStaticEvent("point", new PointEventFactory(loggerFactory));
        betonQuest.registerEvent("removeentity", new RemoveEntityEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvents("run", RunEvent.class);
        betonQuest.registerEvent("runForAll", new RunForAllEventFactory());
        betonQuest.registerEvent("runIndependent", new RunIndependentEventFactory());
        betonQuest.registerEvent("setblock", new SetBlockEventFactory(server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("score", new ScoreboardEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvents("spawn", SpawnMobEvent.class);
        betonQuest.registerNonStaticEvent("stage", new StageEventFactory(betonQuest));
        betonQuest.registerNonStaticEvent("sudo", new SudoEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("tag", new TagPlayerEventFactory(betonQuest, betonQuest.getSaver()));
        betonQuest.registerEvents("take", TakeEvent.class);
        betonQuest.registerNonStaticEvent("teleport", new TeleportEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerEvent("time", new TimeEventFactory(server, scheduler, betonQuest));
        betonQuest.registerEvents("variable", VariableEvent.class);
        betonQuest.registerNonStaticEvent("velocity", new VelocityEventFactory(loggerFactory, server, scheduler, betonQuest));
        betonQuest.registerNonStaticEvent("weather", new WeatherEventFactory(loggerFactory, server, scheduler, betonQuest));
    }

    private void registerObjectives() {
        betonQuest.registerObjectives("action", ActionObjective.class);
        betonQuest.registerObjectives("arrow", ArrowShootObjective.class);
        betonQuest.registerObjectives("block", BlockObjective.class);
        betonQuest.registerObjectives("breed", BreedObjective.class);
        betonQuest.registerObjectives("brew", BrewObjective.class);
        betonQuest.registerObjectives("chestput", ChestPutObjective.class);
        betonQuest.registerObjectives("command", CommandObjective.class);
        betonQuest.registerObjectives("consume", ConsumeObjective.class);
        betonQuest.registerObjectives("craft", CraftingObjective.class);
        betonQuest.registerObjectives("delay", DelayObjective.class);
        betonQuest.registerObjectives("die", DieObjective.class);
        betonQuest.registerObjectives("enchant", EnchantObjective.class);
        betonQuest.registerObjectives("experience", ExperienceObjective.class);
        betonQuest.registerObjectives("fish", FishObjective.class);
        betonQuest.registerObjectives("interact", EntityInteractObjective.class);
        betonQuest.registerObjectives("kill", KillPlayerObjective.class);
        betonQuest.registerObjectives("location", LocationObjective.class);
        betonQuest.registerObjectives("login", LoginObjective.class);
        betonQuest.registerObjectives("logout", LogoutObjective.class);
        betonQuest.registerObjectives("mobkill", MobKillObjective.class);
        betonQuest.registerObjectives("password", PasswordObjective.class);
        betonQuest.registerObjectives("pickup", PickupObjective.class);
        betonQuest.registerObjectives("ride", RideObjective.class);
        betonQuest.registerObjectives("shear", ShearObjective.class);
        betonQuest.registerObjectives("smelt", SmeltingObjective.class);
        betonQuest.registerObjectives("stage", StageObjective.class);
        betonQuest.registerObjectives("step", StepObjective.class);
        betonQuest.registerObjectives("tame", TameObjective.class);
        betonQuest.registerObjectives("variable", VariableObjective.class);
        if (PaperLib.isPaper()) {
            betonQuest.registerObjectives("equip", EquipItemObjective.class);
            betonQuest.registerObjectives("jump", JumpObjective.class);
            betonQuest.registerObjectives("resourcepack", ResourcePackObjective.class);
        }
    }

    private void registerVariables(final VariableTypeRegistry variables) {
        variables.register("condition", new ConditionVariableFactory());
        variables.register("eval", new EvalVariableFactory());
        betonQuest.registerVariable("globalpoint", GlobalPointVariable.class);
        betonQuest.registerVariable("globaltag", GlobalTagVariable.class);
        betonQuest.registerVariable("item", ItemVariable.class);
        betonQuest.registerVariable("itemdurability", ItemDurabilityVariable.class);
        betonQuest.registerVariable("location", LocationVariable.class);
        betonQuest.registerVariable("math", MathVariable.class);
        variables.register("npc", new NpcNameVariableFactory(betonQuest));
        betonQuest.registerVariable("objective", ObjectivePropertyVariable.class);
        betonQuest.registerVariable("point", PointVariable.class);
        variables.register("player", new PlayerNameVariableFactory(loggerFactory));
        betonQuest.registerVariable("randomnumber", RandomNumberVariable.class);
        betonQuest.registerVariable("tag", TagVariable.class);
        betonQuest.registerVariable("version", VersionVariable.class);
    }
}
