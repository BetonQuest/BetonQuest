package org.betonquest.betonquest.quest.registry;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.objective.ActionObjective;
import org.betonquest.betonquest.objective.ArrowShootObjective;
import org.betonquest.betonquest.objective.BlockObjective;
import org.betonquest.betonquest.objective.BreedObjective;
import org.betonquest.betonquest.objective.BrewObjective;
import org.betonquest.betonquest.objective.ChestPutObjective;
import org.betonquest.betonquest.objective.CommandObjective;
import org.betonquest.betonquest.objective.ConsumeObjective;
import org.betonquest.betonquest.objective.CraftingObjective;
import org.betonquest.betonquest.objective.DelayObjective;
import org.betonquest.betonquest.objective.DieObjective;
import org.betonquest.betonquest.objective.EnchantObjective;
import org.betonquest.betonquest.objective.EntityInteractObjective;
import org.betonquest.betonquest.objective.EquipItemObjective;
import org.betonquest.betonquest.objective.ExperienceObjective;
import org.betonquest.betonquest.objective.FishObjective;
import org.betonquest.betonquest.objective.JumpObjective;
import org.betonquest.betonquest.objective.KillPlayerObjective;
import org.betonquest.betonquest.objective.LocationObjective;
import org.betonquest.betonquest.objective.LoginObjective;
import org.betonquest.betonquest.objective.LogoutObjective;
import org.betonquest.betonquest.objective.MobKillObjective;
import org.betonquest.betonquest.objective.PasswordObjective;
import org.betonquest.betonquest.objective.PickupObjective;
import org.betonquest.betonquest.objective.ResourcePackObjective;
import org.betonquest.betonquest.objective.RideObjective;
import org.betonquest.betonquest.objective.ShearObjective;
import org.betonquest.betonquest.objective.SmeltingObjective;
import org.betonquest.betonquest.objective.StageObjective;
import org.betonquest.betonquest.objective.StepObjective;
import org.betonquest.betonquest.objective.TameObjective;
import org.betonquest.betonquest.objective.VariableObjective;
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
import org.betonquest.betonquest.quest.condition.scoreboard.ScoreboardObjectiveConditionFactory;
import org.betonquest.betonquest.quest.condition.scoreboard.ScoreboardTagConditionFactory;
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
import org.betonquest.betonquest.quest.event.scoreboard.ScoreboardObjectiveEventFactory;
import org.betonquest.betonquest.quest.event.scoreboard.ScoreboardTagEventFactory;
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
import org.betonquest.betonquest.quest.registry.type.ObjectiveTypeRegistry;
import org.betonquest.betonquest.quest.registry.type.VariableTypeRegistry;
import org.betonquest.betonquest.quest.variable.condition.ConditionVariableFactory;
import org.betonquest.betonquest.quest.variable.eval.EvalVariableFactory;
import org.betonquest.betonquest.quest.variable.item.ItemDurabilityVariableFactory;
import org.betonquest.betonquest.quest.variable.item.ItemVariableFactory;
import org.betonquest.betonquest.quest.variable.location.LocationVariableFactory;
import org.betonquest.betonquest.quest.variable.math.MathVariableFactory;
import org.betonquest.betonquest.quest.variable.name.NpcNameVariableFactory;
import org.betonquest.betonquest.quest.variable.name.PlayerNameVariableFactory;
import org.betonquest.betonquest.quest.variable.objective.ObjectivePropertyVariableFactory;
import org.betonquest.betonquest.quest.variable.point.GlobalPointVariableFactory;
import org.betonquest.betonquest.quest.variable.point.PointVariableFactory;
import org.betonquest.betonquest.quest.variable.random.RandomNumberVariableFactory;
import org.betonquest.betonquest.quest.variable.tag.GlobalTagVariableFactory;
import org.betonquest.betonquest.quest.variable.tag.TagVariableFactory;
import org.betonquest.betonquest.quest.variable.version.VersionVariableFactory;
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
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Storage for global data.
     */
    private final GlobalData globalData;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory     used in event factories
     * @param server            the server used for primary server thread access.
     * @param scheduler         the scheduler used for primary server thread access
     * @param betonQuest        the plugin used for primary server access and type registration
     * @param questTypeAPI      the Quest Type API
     * @param variableProcessor the variable processor to create new variables
     * @param globalData        the storage providing global data
     * @param dataStorage       the storage providing player data
     */
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest betonQuest,
                          final QuestTypeAPI questTypeAPI, final PluginMessage pluginMessage,
                          final VariableProcessor variableProcessor, final GlobalData globalData,
                          final PlayerDataStorage dataStorage) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.betonQuest = betonQuest;
        this.questTypeAPI = questTypeAPI;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
        this.globalData = globalData;
        this.dataStorage = dataStorage;
        this.data = new PrimaryServerThreadData(server, scheduler, betonQuest);
    }

    /**
     * Registers the Quest Types.
     *
     * @param questTypeRegistries the registry to register the types in
     */
    public void register(final QuestTypeRegistries questTypeRegistries) {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions(questTypeRegistries.condition());
        registerEvents(questTypeRegistries.event());
        registerObjectives(questTypeRegistries.objective());
        registerVariables(questTypeRegistries.variable());
    }

    private void registerConditions(final ConditionTypeRegistry conditionTypes) {
        conditionTypes.register("advancement", new AdvancementConditionFactory(data, loggerFactory));
        conditionTypes.registerCombined("and", new ConjunctionConditionFactory(questTypeAPI));
        conditionTypes.register("armor", new ArmorConditionFactory(loggerFactory, data));
        conditionTypes.register("biome", new BiomeConditionFactory(loggerFactory, data));
        conditionTypes.register("burning", new BurningConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("check", new CheckConditionFactory());
        conditionTypes.registerCombined("chestitem", new ChestItemConditionFactory(data));
        conditionTypes.register("conversation", new ConversationConditionFactory(betonQuest.getFeatureAPI()));
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", new EffectConditionFactory(loggerFactory, data));
        conditionTypes.register("empty", new EmptySlotsConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("entities", new EntityConditionFactory(data));
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
        conditionTypes.register("item", new ItemConditionFactory(loggerFactory, data, dataStorage));
        conditionTypes.register("itemdurability", new ItemDurabilityConditionFactory(loggerFactory, data));
        conditionTypes.register("journal", new JournalConditionFactory(dataStorage, loggerFactory));
        conditionTypes.register("language", new LanguageConditionFactory(dataStorage, pluginMessage));
        conditionTypes.register("location", new LocationConditionFactory(data, loggerFactory));
        conditionTypes.register("looking", new LookingAtConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("mooncycle", new MoonCycleConditionFactory(data, variableProcessor));
        conditionTypes.registerCombined("numbercompare", new NumberCompareConditionFactory());
        conditionTypes.register("objective", new ObjectiveConditionFactory(questTypeAPI));
        conditionTypes.registerCombined("or", new AlternativeConditionFactory(loggerFactory));
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.registerCombined("party", new PartyConditionFactory(questTypeAPI));
        conditionTypes.register("permission", new PermissionConditionFactory(loggerFactory, data));
        conditionTypes.register("point", new PointConditionFactory(dataStorage));
        conditionTypes.registerCombined("random", new RandomConditionFactory(variableProcessor));
        conditionTypes.register("rating", new ArmorRatingConditionFactory(loggerFactory, data));
        conditionTypes.register("realtime", new RealTimeConditionFactory());
        conditionTypes.register("ride", new RideConditionFactory(loggerFactory, data));
        conditionTypes.register("score", new ScoreboardObjectiveConditionFactory(data));
        conditionTypes.register("scoretag", new ScoreboardTagConditionFactory(data, loggerFactory));
        conditionTypes.register("sneak", new SneakConditionFactory(loggerFactory, data));
        conditionTypes.register("stage", new StageConditionFactory(questTypeAPI));
        conditionTypes.register("tag", new TagConditionFactory(dataStorage));
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory(data));
        conditionTypes.registerCombined("time", new TimeConditionFactory(data, variableProcessor));
        conditionTypes.registerCombined("variable", new VariableConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("weather", new WeatherConditionFactory(data, variableProcessor));
        conditionTypes.register("world", new WorldConditionFactory(loggerFactory, data, variableProcessor));
    }

    private void registerEvents(final EventTypeRegistry eventTypes) {
        eventTypes.register("burn", new BurnEventFactory(loggerFactory, data));
        eventTypes.register("cancel", new CancelEventFactory(loggerFactory, betonQuest.getFeatureAPI()));
        eventTypes.register("cancelconversation", new CancelConversationEventFactory(loggerFactory));
        eventTypes.register("chat", new ChatEventFactory(loggerFactory, data));
        eventTypes.registerCombined("chestclear", new ChestClearEventFactory(data));
        eventTypes.registerCombined("chestgive", new ChestGiveEventFactory(data));
        eventTypes.registerCombined("chesttake", new ChestTakeEventFactory(data));
        eventTypes.register("compass", new CompassEventFactory(loggerFactory, dataStorage, server.getPluginManager(), data));
        eventTypes.registerCombined("command", new CommandEventFactory(loggerFactory, data));
        eventTypes.register("conversation", new ConversationEventFactory(loggerFactory, data, pluginMessage));
        eventTypes.register("damage", new DamageEventFactory(loggerFactory, data));
        eventTypes.register("deleffect", new DeleteEffectEventFactory(loggerFactory, data));
        eventTypes.register("deleteglobalpoint", new DeleteGlobalPointEventFactory());
        eventTypes.registerCombined("deletepoint", new DeletePointEventFactory(dataStorage, betonQuest.getSaver()));
        eventTypes.registerCombined("door", new DoorEventFactory(data));
        eventTypes.registerCombined("drop", new DropEventFactory(data));
        eventTypes.register("effect", new EffectEventFactory(loggerFactory, data));
        eventTypes.register("experience", new ExperienceEventFactory(loggerFactory, data));
        eventTypes.registerCombined("explosion", new ExplosionEventFactory(data));
        eventTypes.registerCombined("folder", new FolderEventFactory(betonQuest, loggerFactory, server.getPluginManager(), questTypeAPI));
        eventTypes.registerCombined("first", new FirstEventFactory(questTypeAPI));
        eventTypes.register("give", new GiveEventFactory(loggerFactory, data, dataStorage, pluginMessage));
        eventTypes.register("givejournal", new GiveJournalEventFactory(loggerFactory, dataStorage, data));
        eventTypes.registerCombined("globaltag", new TagGlobalEventFactory(betonQuest));
        eventTypes.registerCombined("globalpoint", new GlobalPointEventFactory(variableProcessor));
        eventTypes.register("hunger", new HungerEventFactory(loggerFactory, data));
        eventTypes.registerCombined("if", new IfElseEventFactory(questTypeAPI));
        eventTypes.register("itemdurability", new ItemDurabilityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("journal", new JournalEventFactory(loggerFactory, pluginMessage, dataStorage,
                InstantSource.system(), betonQuest.getSaver(), betonQuest.getProfileProvider()));
        eventTypes.register("kill", new KillEventFactory(loggerFactory, data));
        eventTypes.register("language", new LanguageEventFactory(dataStorage));
        eventTypes.registerCombined("lever", new LeverEventFactory(data));
        eventTypes.registerCombined("lightning", new LightningEventFactory(data));
        eventTypes.registerCombined("log", new LogEventFactory(loggerFactory));
        eventTypes.register("notify", new NotifyEventFactory(loggerFactory, data, dataStorage));
        eventTypes.registerCombined("notifyall", new NotifyAllEventFactory(loggerFactory, data, dataStorage));
        eventTypes.registerCombined("objective", new ObjectiveEventFactory(betonQuest, loggerFactory, questTypeAPI,
                pluginMessage));
        eventTypes.register("opsudo", new OpSudoEventFactory(loggerFactory, data));
        eventTypes.register("party", new PartyEventFactory(loggerFactory, questTypeAPI));
        eventTypes.registerCombined("pickrandom", new PickRandomEventFactory(questTypeAPI));
        eventTypes.register("point", new PointEventFactory(loggerFactory, variableProcessor, dataStorage,
                pluginMessage));
        eventTypes.registerCombined("removeentity", new RemoveEntityEventFactory(data));
        eventTypes.registerCombined("run", new RunEventFactory());
        eventTypes.register("runForAll", new RunForAllEventFactory(questTypeAPI));
        eventTypes.register("runIndependent", new RunIndependentEventFactory(questTypeAPI));
        eventTypes.registerCombined("setblock", new SetBlockEventFactory(data));
        eventTypes.register("score", new ScoreboardObjectiveEventFactory(data, variableProcessor));
        eventTypes.register("scoretag", new ScoreboardTagEventFactory(loggerFactory, data));
        eventTypes.registerCombined("spawn", new SpawnMobEventFactory(data));
        eventTypes.register("stage", new StageEventFactory(questTypeAPI));
        eventTypes.register("sudo", new SudoEventFactory(loggerFactory, data));
        eventTypes.registerCombined("tag", new TagPlayerEventFactory(dataStorage, betonQuest.getSaver()));
        eventTypes.register("take", new TakeEventFactory(loggerFactory, pluginMessage));
        eventTypes.register("teleport", new TeleportEventFactory(loggerFactory, data));
        eventTypes.registerCombined("time", new TimeEventFactory(server, data, variableProcessor));
        eventTypes.register("variable", new VariableEventFactory(questTypeAPI));
        eventTypes.register("velocity", new VelocityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("weather", new WeatherEventFactory(loggerFactory, data));
    }

    private void registerObjectives(final ObjectiveTypeRegistry objectiveTypes) {
        objectiveTypes.register("action", ActionObjective.class);
        objectiveTypes.register("arrow", ArrowShootObjective.class);
        objectiveTypes.register("block", BlockObjective.class);
        objectiveTypes.register("breed", BreedObjective.class);
        objectiveTypes.register("brew", BrewObjective.class);
        objectiveTypes.register("chestput", ChestPutObjective.class);
        objectiveTypes.register("command", CommandObjective.class);
        objectiveTypes.register("consume", ConsumeObjective.class);
        objectiveTypes.register("craft", CraftingObjective.class);
        objectiveTypes.register("delay", DelayObjective.class);
        objectiveTypes.register("die", DieObjective.class);
        objectiveTypes.register("enchant", EnchantObjective.class);
        objectiveTypes.register("experience", ExperienceObjective.class);
        objectiveTypes.register("fish", FishObjective.class);
        objectiveTypes.register("interact", EntityInteractObjective.class);
        objectiveTypes.register("kill", KillPlayerObjective.class);
        objectiveTypes.register("location", LocationObjective.class);
        objectiveTypes.register("login", LoginObjective.class);
        objectiveTypes.register("logout", LogoutObjective.class);
        objectiveTypes.register("mobkill", MobKillObjective.class);
        objectiveTypes.register("password", PasswordObjective.class);
        objectiveTypes.register("pickup", PickupObjective.class);
        objectiveTypes.register("ride", RideObjective.class);
        objectiveTypes.register("shear", ShearObjective.class);
        objectiveTypes.register("smelt", SmeltingObjective.class);
        objectiveTypes.register("stage", StageObjective.class);
        objectiveTypes.register("step", StepObjective.class);
        objectiveTypes.register("tame", TameObjective.class);
        objectiveTypes.register("variable", VariableObjective.class);
        if (PaperLib.isPaper()) {
            objectiveTypes.register("equip", EquipItemObjective.class);
            objectiveTypes.register("jump", JumpObjective.class);
            objectiveTypes.register("resourcepack", ResourcePackObjective.class);
        }
    }

    private void registerVariables(final VariableTypeRegistry variables) {
        variables.register("condition", new ConditionVariableFactory(questTypeAPI, pluginMessage));
        variables.registerCombined("eval", new EvalVariableFactory(variableProcessor));
        variables.register("globalpoint", new GlobalPointVariableFactory(globalData, loggerFactory.create(GlobalPointVariableFactory.class)));
        variables.register("globaltag", new GlobalTagVariableFactory(globalData, pluginMessage));
        variables.registerCombined("item", new ItemVariableFactory());
        variables.register("itemdurability", new ItemDurabilityVariableFactory());
        variables.register("location", new LocationVariableFactory());
        variables.registerCombined("math", new MathVariableFactory(variableProcessor));
        variables.register("npc", new NpcNameVariableFactory(dataStorage));
        variables.register("objective", new ObjectivePropertyVariableFactory(questTypeAPI));
        variables.register("point", new PointVariableFactory(dataStorage, loggerFactory.create(PointVariableFactory.class)));
        variables.register("player", new PlayerNameVariableFactory());
        variables.registerCombined("randomnumber", new RandomNumberVariableFactory());
        variables.register("tag", new TagVariableFactory(dataStorage, pluginMessage));
        variables.register("version", new VersionVariableFactory(betonQuest));
    }
}
