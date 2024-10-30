package org.betonquest.betonquest.quest.registry;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
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
import org.betonquest.betonquest.quest.condition.armor.ArmorConditionFactory;
import org.betonquest.betonquest.quest.condition.armor.ArmorRatingConditionFactory;
import org.betonquest.betonquest.quest.condition.biome.BiomeConditionFactory;
import org.betonquest.betonquest.quest.condition.block.BlockConditionFactory;
import org.betonquest.betonquest.quest.condition.burning.BurningConditionFactory;
import org.betonquest.betonquest.quest.condition.check.CheckConditionFactory;
import org.betonquest.betonquest.quest.condition.chest.ChestItemConditionFactory;
import org.betonquest.betonquest.quest.condition.conversation.ConversationConditionFactory;
import org.betonquest.betonquest.quest.condition.conversation.InConversationConditionFactory;
import org.betonquest.betonquest.quest.condition.effect.EffectConditionFactory;
import org.betonquest.betonquest.quest.condition.entity.EntityConditionFactory;
import org.betonquest.betonquest.quest.condition.experience.ExperienceConditionFactory;
import org.betonquest.betonquest.quest.condition.facing.FacingConditionFactory;
import org.betonquest.betonquest.quest.condition.flying.FlyingConditionFactory;
import org.betonquest.betonquest.quest.condition.gamemode.GameModeConditionFactory;
import org.betonquest.betonquest.quest.condition.hand.HandConditionFactory;
import org.betonquest.betonquest.quest.condition.health.HealthConditionFactory;
import org.betonquest.betonquest.quest.condition.height.HeightConditionFactory;
import org.betonquest.betonquest.quest.condition.hunger.HungerConditionFactory;
import org.betonquest.betonquest.quest.condition.item.ItemConditionFactory;
import org.betonquest.betonquest.quest.condition.item.ItemDurabilityConditionFactory;
import org.betonquest.betonquest.quest.condition.journal.JournalConditionFactory;
import org.betonquest.betonquest.quest.condition.language.LanguageConditionFactory;
import org.betonquest.betonquest.quest.condition.location.LocationConditionFactory;
import org.betonquest.betonquest.quest.condition.logik.AlternativeConditionFactory;
import org.betonquest.betonquest.quest.condition.logik.ConjunctionConditionFactory;
import org.betonquest.betonquest.quest.condition.looking.LookingAtConditionFactory;
import org.betonquest.betonquest.quest.condition.moon.MoonCycleConditionFactory;
import org.betonquest.betonquest.quest.condition.number.NumberCompareConditionFactory;
import org.betonquest.betonquest.quest.condition.objective.ObjectiveConditionFactory;
import org.betonquest.betonquest.quest.condition.party.PartyConditionFactory;
import org.betonquest.betonquest.quest.condition.permission.PermissionConditionFactory;
import org.betonquest.betonquest.quest.condition.point.GlobalPointConditionFactory;
import org.betonquest.betonquest.quest.condition.point.PointConditionFactory;
import org.betonquest.betonquest.quest.condition.random.RandomConditionFactory;
import org.betonquest.betonquest.quest.condition.ride.RideConditionFactory;
import org.betonquest.betonquest.quest.condition.scoreboard.ScoreboardConditionFactory;
import org.betonquest.betonquest.quest.condition.slots.EmptySlotsConditionFactory;
import org.betonquest.betonquest.quest.condition.sneak.SneakConditionFactory;
import org.betonquest.betonquest.quest.condition.stage.StageConditionFactory;
import org.betonquest.betonquest.quest.condition.tag.GlobalTagConditionFactory;
import org.betonquest.betonquest.quest.condition.tag.TagConditionFactory;
import org.betonquest.betonquest.quest.condition.time.ingame.TimeConditionFactory;
import org.betonquest.betonquest.quest.condition.time.real.DayOfWeekConditionFactory;
import org.betonquest.betonquest.quest.condition.time.real.PartialDateConditionFactory;
import org.betonquest.betonquest.quest.condition.time.real.RealTimeConditionFactory;
import org.betonquest.betonquest.quest.condition.variable.VariableConditionFactory;
import org.betonquest.betonquest.quest.condition.weather.WeatherConditionFactory;
import org.betonquest.betonquest.quest.condition.world.WorldConditionFactory;
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
import org.betonquest.betonquest.quest.event.folder.FolderEventFactory;
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
import org.betonquest.betonquest.quest.event.objective.ObjectiveEventFactory;
import org.betonquest.betonquest.quest.event.party.PartyEventFactory;
import org.betonquest.betonquest.quest.event.point.DeleteGlobalPointEventFactory;
import org.betonquest.betonquest.quest.event.point.DeletePointEventFactory;
import org.betonquest.betonquest.quest.event.point.GlobalPointEventFactory;
import org.betonquest.betonquest.quest.event.point.PointEventFactory;
import org.betonquest.betonquest.quest.event.random.PickRandomEventFactory;
import org.betonquest.betonquest.quest.event.run.RunEventFactory;
import org.betonquest.betonquest.quest.event.run.RunForAllEventFactory;
import org.betonquest.betonquest.quest.event.run.RunIndependentEventFactory;
import org.betonquest.betonquest.quest.event.scoreboard.ScoreboardEventFactory;
import org.betonquest.betonquest.quest.event.setblock.SetBlockEventFactory;
import org.betonquest.betonquest.quest.event.spawn.SpawnMobEventFactory;
import org.betonquest.betonquest.quest.event.stage.StageEventFactory;
import org.betonquest.betonquest.quest.event.tag.TagGlobalEventFactory;
import org.betonquest.betonquest.quest.event.tag.TagPlayerEventFactory;
import org.betonquest.betonquest.quest.event.take.TakeEventFactory;
import org.betonquest.betonquest.quest.event.teleport.TeleportEventFactory;
import org.betonquest.betonquest.quest.event.time.TimeEventFactory;
import org.betonquest.betonquest.quest.event.variable.VariableEventFactory;
import org.betonquest.betonquest.quest.event.velocity.VelocityEventFactory;
import org.betonquest.betonquest.quest.event.weather.WeatherEventFactory;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
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
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory     used in event factories
     * @param server            the server used for primary server thread access.
     * @param scheduler         the scheduler used for primary server thread access
     * @param betonQuest        the plugin used for primary server access and type registration
     * @param variableProcessor the variable processor to create new variables
     */
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest betonQuest,
                          final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.betonQuest = betonQuest;
        this.variableProcessor = variableProcessor;
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
        conditionTypes.registerCombined("and", new ConjunctionConditionFactory());
        conditionTypes.register("armor", new ArmorConditionFactory(loggerFactory, data));
        conditionTypes.register("biome", new BiomeConditionFactory(loggerFactory, data));
        conditionTypes.register("burning", new BurningConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("check", new CheckConditionFactory(loggerFactory.create(CheckConditionFactory.class)));
        conditionTypes.registerCombined("chestitem", new ChestItemConditionFactory(data));
        conditionTypes.register("conversation", new ConversationConditionFactory());
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", new EffectConditionFactory(loggerFactory, data));
        conditionTypes.register("empty", new EmptySlotsConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("entities", new EntityConditionFactory(data, variableProcessor));
        conditionTypes.register("experience", new ExperienceConditionFactory(loggerFactory, data));
        conditionTypes.register("facing", new FacingConditionFactory(loggerFactory, data));
        conditionTypes.register("fly", new FlyingConditionFactory(loggerFactory, data));
        conditionTypes.register("gamemode", new GameModeConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("globalpoint", new GlobalPointConditionFactory(betonQuest.getGlobalData()));
        conditionTypes.register("globaltag", new GlobalTagConditionFactory(betonQuest.getGlobalData()));
        conditionTypes.register("hand", new HandConditionFactory(loggerFactory, data));
        conditionTypes.register("health", new HealthConditionFactory(loggerFactory, data));
        conditionTypes.register("height", new HeightConditionFactory(loggerFactory, data, variableProcessor));
        conditionTypes.register("hunger", new HungerConditionFactory(loggerFactory, data));
        conditionTypes.register("inconversation", new InConversationConditionFactory());
        conditionTypes.register("item", new ItemConditionFactory(loggerFactory, data, betonQuest));
        conditionTypes.register("itemdurability", new ItemDurabilityConditionFactory(loggerFactory, data));
        conditionTypes.register("journal", new JournalConditionFactory(betonQuest, loggerFactory));
        conditionTypes.register("language", new LanguageConditionFactory(betonQuest));
        conditionTypes.register("location", new LocationConditionFactory(data, loggerFactory));
        conditionTypes.register("looking", new LookingAtConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("mooncycle", new MoonCycleConditionFactory(data, variableProcessor));
        conditionTypes.registerCombined("numbercompare", new NumberCompareConditionFactory());
        conditionTypes.register("objective", new ObjectiveConditionFactory(betonQuest));
        conditionTypes.registerCombined("or", new AlternativeConditionFactory(loggerFactory));
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.registerCombined("party", new PartyConditionFactory());
        conditionTypes.register("permission", new PermissionConditionFactory(loggerFactory, data, variableProcessor));
        conditionTypes.register("point", new PointConditionFactory(betonQuest));
        conditionTypes.registerCombined("random", new RandomConditionFactory(variableProcessor));
        conditionTypes.register("rating", new ArmorRatingConditionFactory(loggerFactory, data));
        conditionTypes.register("realtime", new RealTimeConditionFactory());
        conditionTypes.register("ride", new RideConditionFactory(loggerFactory, data));
        conditionTypes.register("score", new ScoreboardConditionFactory(data));
        conditionTypes.register("sneak", new SneakConditionFactory(loggerFactory, data));
        conditionTypes.register("stage", new StageConditionFactory(variableProcessor, betonQuest));
        conditionTypes.register("tag", new TagConditionFactory(betonQuest));
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory(data));
        conditionTypes.registerCombined("time", new TimeConditionFactory(data, variableProcessor));
        conditionTypes.registerCombined("variable", new VariableConditionFactory(loggerFactory, data, variableProcessor));
        conditionTypes.registerCombined("weather", new WeatherConditionFactory(data, variableProcessor));
        conditionTypes.register("world", new WorldConditionFactory(loggerFactory, data, variableProcessor));
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
        eventTypes.registerCombined("folder", new FolderEventFactory(betonQuest, loggerFactory, server.getPluginManager()));
        eventTypes.registerCombined("first", new FirstEventFactory());
        eventTypes.register("give", new GiveEventFactory(loggerFactory, data));
        eventTypes.register("givejournal", new GiveJournalEventFactory(loggerFactory, betonQuest, data));
        eventTypes.registerCombined("globaltag", new TagGlobalEventFactory(betonQuest));
        eventTypes.registerCombined("globalpoint", new GlobalPointEventFactory(variableProcessor));
        eventTypes.register("hunger", new HungerEventFactory(loggerFactory, data));
        eventTypes.registerCombined("if", new IfElseEventFactory());
        eventTypes.register("itemdurability", new ItemDurabilityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("journal", new JournalEventFactory(loggerFactory, betonQuest, InstantSource.system(), betonQuest.getSaver()));
        eventTypes.register("kill", new KillEventFactory(loggerFactory, data));
        eventTypes.register("language", new LanguageEventFactory(betonQuest));
        eventTypes.registerCombined("lever", new LeverEventFactory(data));
        eventTypes.registerCombined("lightning", new LightningEventFactory(data));
        eventTypes.registerCombined("log", new LogEventFactory(loggerFactory, variableProcessor));
        eventTypes.register("notify", new NotifyEventFactory(loggerFactory, data, variableProcessor));
        eventTypes.registerCombined("notifyall", new NotifyAllEventFactory(loggerFactory, data, variableProcessor));
        eventTypes.registerCombined("objective", new ObjectiveEventFactory(betonQuest, loggerFactory));
        eventTypes.register("opsudo", new OpSudoEventFactory(loggerFactory, data));
        eventTypes.register("party", new PartyEventFactory(loggerFactory));
        eventTypes.registerCombined("pickrandom", new PickRandomEventFactory(variableProcessor));
        eventTypes.register("point", new PointEventFactory(loggerFactory, variableProcessor));
        eventTypes.registerCombined("removeentity", new RemoveEntityEventFactory(data, variableProcessor));
        eventTypes.registerCombined("run", new RunEventFactory(betonQuest));
        eventTypes.register("runForAll", new RunForAllEventFactory());
        eventTypes.register("runIndependent", new RunIndependentEventFactory());
        eventTypes.registerCombined("setblock", new SetBlockEventFactory(data));
        eventTypes.register("score", new ScoreboardEventFactory(data, variableProcessor));
        eventTypes.registerCombined("spawn", new SpawnMobEventFactory(data, variableProcessor));
        eventTypes.register("stage", new StageEventFactory(betonQuest, variableProcessor));
        eventTypes.register("sudo", new SudoEventFactory(loggerFactory, data));
        eventTypes.registerCombined("tag", new TagPlayerEventFactory(betonQuest, betonQuest.getSaver()));
        eventTypes.register("take", new TakeEventFactory(loggerFactory));
        eventTypes.register("teleport", new TeleportEventFactory(loggerFactory, data));
        eventTypes.registerCombined("time", new TimeEventFactory(server, data, variableProcessor));
        eventTypes.register("variable", new VariableEventFactory(betonQuest, variableProcessor));
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
        variables.registerCombined("eval", new EvalVariableFactory(variableProcessor));
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
