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
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
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
        registerEvents(questTypeRegistries.getEventTypes());
        registerObjectives();
        registerVariables(questTypeRegistries.getVariableTypes());
    }

    private void registerConditions(final ConditionTypeRegistry conditionTypes) {
        conditionTypes.register("advancement", new AdvancementConditionFactory(data, loggerFactory));
        conditionTypes.register("and", ConjunctionCondition.class);
        conditionTypes.register("armor", ArmorCondition.class);
        conditionTypes.register("biome", BiomeCondition.class);
        conditionTypes.register("burning", BurningCondition.class);
        conditionTypes.register("check", CheckCondition.class);
        conditionTypes.register("chestitem", ChestItemCondition.class);
        conditionTypes.register("conversation", ConversationCondition.class);
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", EffectCondition.class);
        conditionTypes.register("empty", EmptySlotsCondition.class);
        conditionTypes.register("entities", EntityCondition.class);
        conditionTypes.register("experience", ExperienceCondition.class);
        conditionTypes.register("facing", FacingCondition.class);
        conditionTypes.register("fly", FlyingCondition.class);
        conditionTypes.register("gamemode", GameModeCondition.class);
        conditionTypes.register("globalpoint", GlobalPointCondition.class);
        conditionTypes.register("globaltag", GlobalTagCondition.class);
        conditionTypes.register("hand", HandCondition.class);
        conditionTypes.register("health", HealthCondition.class);
        conditionTypes.register("height", HeightCondition.class);
        conditionTypes.register("hunger", HungerCondition.class);
        conditionTypes.register("inconversation", InConversationCondition.class);
        conditionTypes.register("item", ItemCondition.class);
        conditionTypes.register("itemdurability", ItemDurabilityCondition.class);
        conditionTypes.register("journal", JournalCondition.class);
        conditionTypes.register("language", LanguageCondition.class);
        conditionTypes.register("location", LocationCondition.class);
        conditionTypes.register("looking", LookingAtCondition.class);
        conditionTypes.register("mooncycle", MooncycleCondition.class);
        conditionTypes.register("numbercompare", NumberCompareCondition.class);
        conditionTypes.register("objective", ObjectiveCondition.class);
        conditionTypes.register("or", AlternativeCondition.class);
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.register("party", PartyCondition.class);
        conditionTypes.register("permission", PermissionCondition.class);
        conditionTypes.register("point", PointCondition.class);
        conditionTypes.register("random", RandomCondition.class);
        conditionTypes.register("rating", ArmorRatingCondition.class);
        conditionTypes.register("realtime", RealTimeCondition.class);
        conditionTypes.register("ride", RideCondition.class);
        conditionTypes.register("score", ScoreboardCondition.class);
        conditionTypes.register("sneak", SneakCondition.class);
        conditionTypes.register("stage", StageCondition.class);
        conditionTypes.register("tag", TagCondition.class);
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory(data));
        conditionTypes.register("time", TimeCondition.class);
        conditionTypes.register("variable", VariableCondition.class);
        conditionTypes.register("weather", WeatherCondition.class);
        conditionTypes.register("world", WorldCondition.class);
    }

    private void registerEvents(final EventTypeRegistry eventTypes) {
        eventTypes.register("burn", new BurnEventFactory(loggerFactory, data));
        eventTypes.register("cancel", new CancelEventFactory(loggerFactory));
        eventTypes.register("cancelconversation", new CancelConversationEventFactory(loggerFactory));
        eventTypes.register("chat", new ChatEventFactory(loggerFactory, data));
        eventTypes.registerCombined("chestclear", new ChestClearEventFactory(data));
        eventTypes.registerCombined("chestgive", new ChestGiveEventFactory(data));
        eventTypes.registerCombined("chesttake", new ChestTakeEventFactory(data));
        eventTypes.register("compass", new CompassEventFactory(loggerFactory, betonQuest, server.getPluginManager(), data));
        eventTypes.registerCombined("command", new CommandEventFactory(loggerFactory, data));
        eventTypes.register("conversation", new ConversationEventFactory(loggerFactory, data));
        eventTypes.register("damage", new DamageEventFactory(loggerFactory, data));
        eventTypes.register("deleffect", new DeleteEffectEventFactory(loggerFactory, data));
        eventTypes.register("deleteglobalpoint", new DeleteGlobalPointEventFactory());
        eventTypes.registerCombined("deletepoint", new DeletePointEventFactory(betonQuest, betonQuest.getSaver()));
        eventTypes.registerCombined("door", new DoorEventFactory(data));
        eventTypes.registerCombined("drop", new DropEventFactory(data));
        eventTypes.register("effect", new EffectEventFactory(loggerFactory, data));
        eventTypes.register("experience", new ExperienceEventFactory(loggerFactory, data));
        eventTypes.registerCombined("explosion", new ExplosionEventFactory(data));
        eventTypes.register("folder", FolderEvent.class);
        eventTypes.registerCombined("first", new FirstEventFactory());
        eventTypes.register("give", new GiveEventFactory(loggerFactory, data));
        eventTypes.register("givejournal", new GiveJournalEventFactory(loggerFactory, betonQuest, data));
        eventTypes.registerCombined("globaltag", new TagGlobalEventFactory(betonQuest));
        eventTypes.registerCombined("globalpoint", new GlobalPointEventFactory(betonQuest.getVariableProcessor()));
        eventTypes.register("hunger", new HungerEventFactory(loggerFactory, data));
        eventTypes.registerCombined("if", new IfElseEventFactory());
        eventTypes.register("itemdurability", new ItemDurabilityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("journal", new JournalEventFactory(loggerFactory, betonQuest, InstantSource.system(), betonQuest.getSaver()));
        eventTypes.register("kill", new KillEventFactory(loggerFactory, data));
        eventTypes.register("language", new LanguageEventFactory(betonQuest));
        eventTypes.registerCombined("lever", new LeverEventFactory(data));
        eventTypes.registerCombined("lightning", new LightningEventFactory(data));
        eventTypes.registerCombined("log", new LogEventFactory(loggerFactory, betonQuest.getVariableProcessor()));
        eventTypes.register("notify", new NotifyEventFactory(loggerFactory, data, betonQuest.getVariableProcessor()));
        eventTypes.registerCombined("notifyall", new NotifyAllEventFactory(loggerFactory, data, betonQuest.getVariableProcessor()));
        eventTypes.register("objective", ObjectiveEvent.class);
        eventTypes.register("opsudo", new OpSudoEventFactory(loggerFactory, data));
        eventTypes.register("party", new PartyEventFactory(loggerFactory));
        eventTypes.registerCombined("pickrandom", new PickRandomEventFactory(betonQuest.getVariableProcessor()));
        eventTypes.register("point", new PointEventFactory(loggerFactory));
        eventTypes.registerCombined("removeentity", new RemoveEntityEventFactory(data, betonQuest.getVariableProcessor()));
        eventTypes.register("run", RunEvent.class);
        eventTypes.register("runForAll", new RunForAllEventFactory());
        eventTypes.register("runIndependent", new RunIndependentEventFactory());
        eventTypes.registerCombined("setblock", new SetBlockEventFactory(data));
        eventTypes.register("score", new ScoreboardEventFactory(data));
        eventTypes.register("spawn", SpawnMobEvent.class);
        eventTypes.register("stage", new StageEventFactory(betonQuest));
        eventTypes.register("sudo", new SudoEventFactory(loggerFactory, data));
        eventTypes.registerCombined("tag", new TagPlayerEventFactory(betonQuest, betonQuest.getSaver()));
        eventTypes.register("take", TakeEvent.class);
        eventTypes.register("teleport", new TeleportEventFactory(loggerFactory, data));
        eventTypes.registerCombined("time", new TimeEventFactory(server, data, betonQuest.getVariableProcessor()));
        eventTypes.register("variable", VariableEvent.class);
        eventTypes.register("velocity", new VelocityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("weather", new WeatherEventFactory(loggerFactory, data));
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
        variables.registerCombined("eval", new EvalVariableFactory(betonQuest.getVariableProcessor()));
        variables.register("globalpoint", GlobalPointVariable.class);
        variables.register("globaltag", GlobalTagVariable.class);
        variables.register("item", ItemVariable.class);
        variables.register("itemdurability", ItemDurabilityVariable.class);
        variables.register("location", LocationVariable.class);
        variables.register("math", MathVariable.class);
        variables.register("npc", new NpcNameVariableFactory(betonQuest));
        variables.register("objective", ObjectivePropertyVariable.class);
        variables.register("point", PointVariable.class);
        variables.register("player", new PlayerNameVariableFactory(loggerFactory));
        variables.register("randomnumber", RandomNumberVariable.class);
        variables.register("tag", TagVariable.class);
        variables.register("version", VersionVariable.class);
    }
}
