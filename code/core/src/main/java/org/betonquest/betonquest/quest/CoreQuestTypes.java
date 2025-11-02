package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.quest.BaseQuestTypeRegistries;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.VariableTypeRegistry;
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
import org.betonquest.betonquest.quest.condition.eval.EvalConditionFactory;
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
import org.betonquest.betonquest.quest.condition.moon.MoonPhaseConditionFactory;
import org.betonquest.betonquest.quest.condition.npc.NpcDistanceConditionFactory;
import org.betonquest.betonquest.quest.condition.npc.NpcLocationConditionFactory;
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
import org.betonquest.betonquest.quest.event.eval.EvalEventFactory;
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
import org.betonquest.betonquest.quest.event.npc.NpcTeleportEventFactory;
import org.betonquest.betonquest.quest.event.npc.UpdateVisibilityNowEventFactory;
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
import org.betonquest.betonquest.quest.objective.action.ActionObjectiveFactory;
import org.betonquest.betonquest.quest.objective.arrow.ArrowShootObjectiveFactory;
import org.betonquest.betonquest.quest.objective.block.BlockObjectiveFactory;
import org.betonquest.betonquest.quest.objective.breed.BreedObjectiveFactory;
import org.betonquest.betonquest.quest.objective.brew.BrewObjectiveFactory;
import org.betonquest.betonquest.quest.objective.chestput.ChestPutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.command.CommandObjectiveFactory;
import org.betonquest.betonquest.quest.objective.consume.ConsumeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.crafting.CraftingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.delay.DelayObjectiveFactory;
import org.betonquest.betonquest.quest.objective.die.DieObjectiveFactory;
import org.betonquest.betonquest.quest.objective.enchant.EnchantObjectiveFactory;
import org.betonquest.betonquest.quest.objective.equip.EquipItemObjectiveFactory;
import org.betonquest.betonquest.quest.objective.experience.ExperienceObjectiveFactory;
import org.betonquest.betonquest.quest.objective.fish.FishObjectiveFactory;
import org.betonquest.betonquest.quest.objective.interact.EntityInteractObjectiveFactory;
import org.betonquest.betonquest.quest.objective.jump.JumpObjectiveFactory;
import org.betonquest.betonquest.quest.objective.kill.KillPlayerObjectiveFactory;
import org.betonquest.betonquest.quest.objective.kill.MobKillObjectiveFactory;
import org.betonquest.betonquest.quest.objective.location.LocationObjectiveFactory;
import org.betonquest.betonquest.quest.objective.login.LoginObjectiveFactory;
import org.betonquest.betonquest.quest.objective.logout.LogoutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.npc.NpcInteractObjectiveFactory;
import org.betonquest.betonquest.quest.objective.npc.NpcRangeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.password.PasswordObjectiveFactory;
import org.betonquest.betonquest.quest.objective.pickup.PickupObjectiveFactory;
import org.betonquest.betonquest.quest.objective.resourcepack.ResourcepackObjectiveFactory;
import org.betonquest.betonquest.quest.objective.ride.RideObjectiveFactory;
import org.betonquest.betonquest.quest.objective.shear.ShearObjectiveFactory;
import org.betonquest.betonquest.quest.objective.smelt.SmeltingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.stage.StageObjectiveFactory;
import org.betonquest.betonquest.quest.objective.step.StepObjectiveFactory;
import org.betonquest.betonquest.quest.objective.tame.TameObjectiveFactory;
import org.betonquest.betonquest.quest.objective.timer.TimerObjectiveFactory;
import org.betonquest.betonquest.quest.objective.variable.VariableObjectiveFactory;
import org.betonquest.betonquest.quest.variable.condition.ConditionVariableFactory;
import org.betonquest.betonquest.quest.variable.constant.ConstantVariableFactory;
import org.betonquest.betonquest.quest.variable.eval.EvalVariableFactory;
import org.betonquest.betonquest.quest.variable.item.ItemDurabilityVariableFactory;
import org.betonquest.betonquest.quest.variable.item.ItemVariableFactory;
import org.betonquest.betonquest.quest.variable.location.LocationVariableFactory;
import org.betonquest.betonquest.quest.variable.math.MathVariableFactory;
import org.betonquest.betonquest.quest.variable.name.PlayerNameVariableFactory;
import org.betonquest.betonquest.quest.variable.name.QuesterVariableFactory;
import org.betonquest.betonquest.quest.variable.npc.NpcVariableFactory;
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
@SuppressWarnings("PMD.NcssCount")
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
    private final QuestTypeApi questTypeApi;

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
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Factory to create new Player Data.
     */
    private final PlayerDataFactory playerDataFactory;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory     used in factories
     * @param server            the server used for primary server thread access.
     * @param scheduler         the scheduler used for primary server thread access
     * @param betonQuest        the plugin used for primary server access and type registration
     * @param questTypeApi      the Quest Type API
     * @param pluginMessage     the plugin message instance
     * @param variableProcessor the variable processor to create new variables
     * @param globalData        the storage providing global data
     * @param dataStorage       the storage providing player data
     * @param profileProvider   the profile provider instance
     * @param languageProvider  the language provider to get the default language
     * @param playerDataFactory the factory to create player data
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest betonQuest,
                          final QuestTypeApi questTypeApi, final PluginMessage pluginMessage,
                          final VariableProcessor variableProcessor, final GlobalData globalData,
                          final PlayerDataStorage dataStorage, final ProfileProvider profileProvider,
                          final LanguageProvider languageProvider, final PlayerDataFactory playerDataFactory) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.betonQuest = betonQuest;
        this.questTypeApi = questTypeApi;
        this.pluginMessage = pluginMessage;
        this.variableProcessor = variableProcessor;
        this.globalData = globalData;
        this.dataStorage = dataStorage;
        this.profileProvider = profileProvider;
        this.languageProvider = languageProvider;
        this.playerDataFactory = playerDataFactory;
        this.data = new PrimaryServerThreadData(server, scheduler, betonQuest);
    }

    /**
     * Registers the Quest Types.
     *
     * @param questTypeRegistries the registry to register the types in
     */
    public void register(final BaseQuestTypeRegistries questTypeRegistries) {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions(questTypeRegistries.condition());
        registerEvents(questTypeRegistries.event());
        registerObjectives(questTypeRegistries.objective());
        registerVariables(questTypeRegistries.variable());
    }

    private void registerConditions(final ConditionTypeRegistry conditionTypes) {
        conditionTypes.register("advancement", new AdvancementConditionFactory(data, loggerFactory));
        conditionTypes.registerCombined("and", new ConjunctionConditionFactory(questTypeApi));
        conditionTypes.register("armor", new ArmorConditionFactory(loggerFactory, data));
        conditionTypes.register("biome", new BiomeConditionFactory(loggerFactory, data));
        conditionTypes.register("burning", new BurningConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("check", new CheckConditionFactory(betonQuest.getQuestPackageManager(), conditionTypes));
        conditionTypes.registerCombined("chestitem", new ChestItemConditionFactory(data));
        conditionTypes.register("conversation", new ConversationConditionFactory(betonQuest.getFeatureApi()));
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", new EffectConditionFactory(loggerFactory, data));
        conditionTypes.register("empty", new EmptySlotsConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("entities", new EntityConditionFactory(data));
        conditionTypes.registerCombined("eval", new EvalConditionFactory(betonQuest.getQuestPackageManager(), conditionTypes));
        conditionTypes.register("experience", new ExperienceConditionFactory(loggerFactory, data));
        conditionTypes.register("facing", new FacingConditionFactory(loggerFactory, data));
        conditionTypes.register("fly", new FlyingConditionFactory(loggerFactory, data));
        conditionTypes.register("gamemode", new GameModeConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("globalpoint", new GlobalPointConditionFactory(betonQuest.getGlobalData()));
        conditionTypes.register("globaltag", new GlobalTagConditionFactory(betonQuest.getGlobalData()));
        conditionTypes.register("hand", new HandConditionFactory(loggerFactory, data));
        conditionTypes.register("health", new HealthConditionFactory(loggerFactory, data));
        conditionTypes.register("height", new HeightConditionFactory(loggerFactory, data));
        conditionTypes.register("hunger", new HungerConditionFactory(loggerFactory, data));
        conditionTypes.register("inconversation", new InConversationConditionFactory());
        conditionTypes.register("item", new ItemConditionFactory(loggerFactory, data, dataStorage));
        conditionTypes.register("itemdurability", new ItemDurabilityConditionFactory(loggerFactory, data));
        conditionTypes.register("journal", new JournalConditionFactory(dataStorage, loggerFactory));
        conditionTypes.register("language", new LanguageConditionFactory(dataStorage, languageProvider, pluginMessage));
        conditionTypes.register("location", new LocationConditionFactory(data, loggerFactory));
        conditionTypes.register("looking", new LookingAtConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("moonphase", new MoonPhaseConditionFactory(data));
        conditionTypes.register("npcdistance", new NpcDistanceConditionFactory(betonQuest.getFeatureApi(), data, loggerFactory));
        conditionTypes.registerCombined("npclocation", new NpcLocationConditionFactory(betonQuest.getFeatureApi(), data));
        conditionTypes.registerCombined("numbercompare", new NumberCompareConditionFactory());
        conditionTypes.register("objective", new ObjectiveConditionFactory(questTypeApi));
        conditionTypes.registerCombined("or", new AlternativeConditionFactory(loggerFactory, questTypeApi));
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.registerCombined("party", new PartyConditionFactory(questTypeApi, profileProvider));
        conditionTypes.register("permission", new PermissionConditionFactory(loggerFactory, data));
        conditionTypes.register("point", new PointConditionFactory(dataStorage));
        conditionTypes.registerCombined("random", new RandomConditionFactory());
        conditionTypes.register("rating", new ArmorRatingConditionFactory(loggerFactory, data));
        conditionTypes.register("realtime", new RealTimeConditionFactory());
        conditionTypes.register("ride", new RideConditionFactory(loggerFactory, data));
        conditionTypes.register("score", new ScoreboardObjectiveConditionFactory(data));
        conditionTypes.register("scoretag", new ScoreboardTagConditionFactory(data, loggerFactory));
        conditionTypes.register("sneak", new SneakConditionFactory(loggerFactory, data));
        conditionTypes.register("stage", new StageConditionFactory(questTypeApi));
        conditionTypes.register("tag", new TagConditionFactory(dataStorage));
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory(data));
        conditionTypes.registerCombined("time", new TimeConditionFactory(data));
        conditionTypes.registerCombined("variable", new VariableConditionFactory(loggerFactory, data));
        conditionTypes.registerCombined("weather", new WeatherConditionFactory(data, variableProcessor));
        conditionTypes.register("world", new WorldConditionFactory(loggerFactory, data));
    }

    private void registerEvents(final EventTypeRegistry eventTypes) {
        eventTypes.register("burn", new BurnEventFactory(loggerFactory, data));
        eventTypes.register("cancel", new CancelEventFactory(loggerFactory, betonQuest.getFeatureApi()));
        eventTypes.register("cancelconversation", new CancelConversationEventFactory(loggerFactory));
        eventTypes.register("chat", new ChatEventFactory(loggerFactory, data));
        eventTypes.registerCombined("chestclear", new ChestClearEventFactory(data));
        eventTypes.registerCombined("chestgive", new ChestGiveEventFactory(data));
        eventTypes.registerCombined("chesttake", new ChestTakeEventFactory(data));
        eventTypes.register("compass", new CompassEventFactory(betonQuest.getFeatureApi(), dataStorage, data));
        eventTypes.registerCombined("command", new CommandEventFactory(loggerFactory, data));
        eventTypes.register("conversation", new ConversationEventFactory(loggerFactory, betonQuest.getQuestPackageManager(), data, pluginMessage));
        eventTypes.register("damage", new DamageEventFactory(loggerFactory, data));
        eventTypes.register("deleffect", new DeleteEffectEventFactory(loggerFactory, data));
        eventTypes.registerCombined("deleteglobalpoint", new DeleteGlobalPointEventFactory(globalData));
        eventTypes.registerCombined("deletepoint", new DeletePointEventFactory(dataStorage, betonQuest.getSaver(), profileProvider));
        eventTypes.registerCombined("door", new DoorEventFactory(data));
        eventTypes.registerCombined("drop", new DropEventFactory(profileProvider, data));
        eventTypes.register("effect", new EffectEventFactory(loggerFactory, data));
        eventTypes.registerCombined("eval", new EvalEventFactory(betonQuest.getQuestPackageManager(), eventTypes));
        eventTypes.register("experience", new ExperienceEventFactory(loggerFactory, data));
        eventTypes.registerCombined("explosion", new ExplosionEventFactory(data));
        eventTypes.registerCombined("folder", new FolderEventFactory(betonQuest, loggerFactory, server.getPluginManager(), questTypeApi));
        eventTypes.registerCombined("first", new FirstEventFactory(questTypeApi));
        eventTypes.register("give", new GiveEventFactory(loggerFactory, data, dataStorage, pluginMessage));
        eventTypes.register("givejournal", new GiveJournalEventFactory(loggerFactory, dataStorage, data));
        eventTypes.registerCombined("globaltag", new TagGlobalEventFactory(betonQuest));
        eventTypes.registerCombined("globalpoint", new GlobalPointEventFactory(globalData));
        eventTypes.register("hunger", new HungerEventFactory(loggerFactory, data));
        eventTypes.registerCombined("if", new IfElseEventFactory(questTypeApi));
        eventTypes.register("itemdurability", new ItemDurabilityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("journal", new JournalEventFactory(loggerFactory, pluginMessage, dataStorage,
                InstantSource.system(), betonQuest.getSaver(), profileProvider));
        eventTypes.register("kill", new KillEventFactory(loggerFactory, data));
        eventTypes.register("language", new LanguageEventFactory(dataStorage));
        eventTypes.registerCombined("lever", new LeverEventFactory(data));
        eventTypes.registerCombined("lightning", new LightningEventFactory(data));
        eventTypes.registerCombined("log", new LogEventFactory(loggerFactory));
        eventTypes.register("notify", new NotifyEventFactory(loggerFactory, data, betonQuest.getTextParser(), dataStorage, languageProvider));
        eventTypes.registerCombined("notifyall", new NotifyAllEventFactory(loggerFactory, data, betonQuest.getTextParser(), dataStorage, profileProvider, languageProvider));
        eventTypes.registerCombined("npcteleport", new NpcTeleportEventFactory(betonQuest.getFeatureApi(), data));
        eventTypes.registerCombined("objective", new ObjectiveEventFactory(betonQuest, loggerFactory, questTypeApi, playerDataFactory));
        eventTypes.register("opsudo", new OpSudoEventFactory(loggerFactory, data));
        eventTypes.register("party", new PartyEventFactory(loggerFactory, questTypeApi, profileProvider));
        eventTypes.registerCombined("pickrandom", new PickRandomEventFactory(betonQuest.getQuestPackageManager(), questTypeApi));
        eventTypes.register("point", new PointEventFactory(loggerFactory, dataStorage,
                pluginMessage));
        eventTypes.registerCombined("removeentity", new RemoveEntityEventFactory(data));
        eventTypes.registerCombined("run", new RunEventFactory(betonQuest.getQuestPackageManager(), eventTypes));
        eventTypes.register("runForAll", new RunForAllEventFactory(questTypeApi, profileProvider));
        eventTypes.register("runIndependent", new RunIndependentEventFactory(questTypeApi));
        eventTypes.registerCombined("setblock", new SetBlockEventFactory(data));
        eventTypes.register("score", new ScoreboardObjectiveEventFactory(data));
        eventTypes.register("scoretag", new ScoreboardTagEventFactory(loggerFactory, data));
        eventTypes.registerCombined("spawn", new SpawnMobEventFactory(data));
        eventTypes.register("stage", new StageEventFactory(questTypeApi));
        eventTypes.register("sudo", new SudoEventFactory(loggerFactory, data));
        eventTypes.registerCombined("tag", new TagPlayerEventFactory(dataStorage, betonQuest.getSaver(), profileProvider));
        eventTypes.register("take", new TakeEventFactory(loggerFactory, pluginMessage));
        eventTypes.register("teleport", new TeleportEventFactory(loggerFactory, data));
        eventTypes.registerCombined("time", new TimeEventFactory(server, data));
        eventTypes.register("updatevisibility", new UpdateVisibilityNowEventFactory(betonQuest.getFeatureApi().getNpcHider(), loggerFactory, data));
        eventTypes.register("variable", new VariableEventFactory(questTypeApi));
        eventTypes.register("velocity", new VelocityEventFactory(loggerFactory, data));
        eventTypes.registerCombined("weather", new WeatherEventFactory(loggerFactory, data));
    }

    private void registerObjectives(final FeatureTypeRegistry<Objective> objectiveTypes) {
        objectiveTypes.register("action", new ActionObjectiveFactory());
        objectiveTypes.register("arrow", new ArrowShootObjectiveFactory());
        objectiveTypes.register("block", new BlockObjectiveFactory(loggerFactory, pluginMessage));
        objectiveTypes.register("breed", new BreedObjectiveFactory());
        objectiveTypes.register("brew", new BrewObjectiveFactory(profileProvider));
        objectiveTypes.register("chestput", new ChestPutObjectiveFactory(loggerFactory, pluginMessage));
        objectiveTypes.register("command", new CommandObjectiveFactory());
        objectiveTypes.register("consume", new ConsumeObjectiveFactory());
        objectiveTypes.register("craft", new CraftingObjectiveFactory());
        objectiveTypes.register("delay", new DelayObjectiveFactory());
        objectiveTypes.register("die", new DieObjectiveFactory());
        objectiveTypes.register("enchant", new EnchantObjectiveFactory());
        objectiveTypes.register("experience", new ExperienceObjectiveFactory(loggerFactory, pluginMessage));
        objectiveTypes.register("fish", new FishObjectiveFactory());
        objectiveTypes.register("interact", new EntityInteractObjectiveFactory());
        objectiveTypes.register("kill", new KillPlayerObjectiveFactory());
        objectiveTypes.register("location", new LocationObjectiveFactory());
        objectiveTypes.register("login", new LoginObjectiveFactory());
        objectiveTypes.register("logout", new LogoutObjectiveFactory());
        objectiveTypes.register("mobkill", new MobKillObjectiveFactory());
        objectiveTypes.register("npcinteract", new NpcInteractObjectiveFactory());
        objectiveTypes.register("npcrange", new NpcRangeObjectiveFactory());
        objectiveTypes.register("password", new PasswordObjectiveFactory());
        objectiveTypes.register("pickup", new PickupObjectiveFactory());
        objectiveTypes.register("ride", new RideObjectiveFactory());
        objectiveTypes.register("shear", new ShearObjectiveFactory());
        objectiveTypes.register("smelt", new SmeltingObjectiveFactory());
        objectiveTypes.register("stage", new StageObjectiveFactory());
        objectiveTypes.register("step", new StepObjectiveFactory());
        objectiveTypes.register("tame", new TameObjectiveFactory());
        objectiveTypes.register("timer", new TimerObjectiveFactory(questTypeApi));
        objectiveTypes.register("variable", new VariableObjectiveFactory());
        objectiveTypes.register("equip", new EquipItemObjectiveFactory());
        objectiveTypes.register("jump", new JumpObjectiveFactory());
        objectiveTypes.register("resourcepack", new ResourcepackObjectiveFactory());
    }

    private void registerVariables(final VariableTypeRegistry variables) {
        variables.register("condition", new ConditionVariableFactory(questTypeApi, pluginMessage));
        variables.registerCombined("constant", new ConstantVariableFactory());
        variables.registerCombined("eval", new EvalVariableFactory());
        variables.register("globalpoint", new GlobalPointVariableFactory(globalData, loggerFactory.create(GlobalPointVariableFactory.class)));
        variables.register("globaltag", new GlobalTagVariableFactory(globalData, pluginMessage));
        variables.registerCombined("item", new ItemVariableFactory(betonQuest.getPlayerDataStorage()));
        variables.register("itemdurability", new ItemDurabilityVariableFactory());
        variables.register("location", new LocationVariableFactory());
        variables.registerCombined("math", new MathVariableFactory(variableProcessor));
        variables.registerCombined("npc", new NpcVariableFactory(betonQuest.getFeatureApi()));
        variables.register("objective", new ObjectivePropertyVariableFactory(questTypeApi));
        variables.register("point", new PointVariableFactory(dataStorage, loggerFactory.create(PointVariableFactory.class)));
        variables.register("player", new PlayerNameVariableFactory());
        variables.register("quester", new QuesterVariableFactory());
        variables.registerCombined("randomnumber", new RandomNumberVariableFactory());
        variables.register("tag", new TagVariableFactory(dataStorage, pluginMessage));
        variables.register("version", new VersionVariableFactory(betonQuest));
    }
}
