package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.identifier.ConversationOptionIdentifier;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.MenuIdentifier;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;
import org.betonquest.betonquest.api.kernel.FeatureRegistry;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.id.action.ActionIdentifierFactory;
import org.betonquest.betonquest.id.cancel.QuestCancelerIdentifierFactory;
import org.betonquest.betonquest.id.compass.CompassIdentifierFactory;
import org.betonquest.betonquest.id.condition.ConditionIdentifierFactory;
import org.betonquest.betonquest.id.conversation.ConversationIdentifierFactory;
import org.betonquest.betonquest.id.conversation.ConversationOptionIdentifierFactory;
import org.betonquest.betonquest.id.item.ItemIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalEntryIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalMainPageIdentifierFactory;
import org.betonquest.betonquest.id.menu.MenuIdentifierFactory;
import org.betonquest.betonquest.id.menu.MenuItemIdentifierFactory;
import org.betonquest.betonquest.id.npc.NpcIdentifierFactory;
import org.betonquest.betonquest.id.objective.ObjectiveIdentifierFactory;
import org.betonquest.betonquest.id.placeholder.PlaceholderIdentifierFactory;
import org.betonquest.betonquest.id.schedule.ScheduleIdentifierFactory;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.BaseQuestTypeRegistries;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.IdentifierTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.betonquest.betonquest.quest.action.burn.BurnActionFactory;
import org.betonquest.betonquest.quest.action.cancel.CancelActionFactory;
import org.betonquest.betonquest.quest.action.chat.ChatActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestClearActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestGiveActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestTakeActionFactory;
import org.betonquest.betonquest.quest.action.command.CommandActionFactory;
import org.betonquest.betonquest.quest.action.command.OpSudoActionFactory;
import org.betonquest.betonquest.quest.action.command.SudoActionFactory;
import org.betonquest.betonquest.quest.action.compass.CompassActionFactory;
import org.betonquest.betonquest.quest.action.conversation.CancelConversationActionFactory;
import org.betonquest.betonquest.quest.action.conversation.ConversationActionFactory;
import org.betonquest.betonquest.quest.action.damage.DamageActionFactory;
import org.betonquest.betonquest.quest.action.door.DoorActionFactory;
import org.betonquest.betonquest.quest.action.drop.DropActionFactory;
import org.betonquest.betonquest.quest.action.effect.DeleteEffectActionFactory;
import org.betonquest.betonquest.quest.action.effect.EffectActionFactory;
import org.betonquest.betonquest.quest.action.entity.RemoveEntityActionFactory;
import org.betonquest.betonquest.quest.action.eval.EvalActionFactory;
import org.betonquest.betonquest.quest.action.experience.ExperienceActionFactory;
import org.betonquest.betonquest.quest.action.explosion.ExplosionActionFactory;
import org.betonquest.betonquest.quest.action.folder.FolderActionFactory;
import org.betonquest.betonquest.quest.action.give.GiveActionFactory;
import org.betonquest.betonquest.quest.action.hunger.HungerActionFactory;
import org.betonquest.betonquest.quest.action.item.ItemDurabilityActionFactory;
import org.betonquest.betonquest.quest.action.journal.GiveJournalActionFactory;
import org.betonquest.betonquest.quest.action.journal.JournalActionFactory;
import org.betonquest.betonquest.quest.action.kill.KillActionFactory;
import org.betonquest.betonquest.quest.action.language.LanguageActionFactory;
import org.betonquest.betonquest.quest.action.lever.LeverActionFactory;
import org.betonquest.betonquest.quest.action.lightning.LightningActionFactory;
import org.betonquest.betonquest.quest.action.log.LogActionFactory;
import org.betonquest.betonquest.quest.action.logic.FirstActionFactory;
import org.betonquest.betonquest.quest.action.logic.IfElseActionFactory;
import org.betonquest.betonquest.quest.action.notify.NotifyActionFactory;
import org.betonquest.betonquest.quest.action.notify.NotifyAllActionFactory;
import org.betonquest.betonquest.quest.action.npc.NpcTeleportActionFactory;
import org.betonquest.betonquest.quest.action.npc.UpdateVisibilityNowActionFactory;
import org.betonquest.betonquest.quest.action.objective.ObjectiveActionFactory;
import org.betonquest.betonquest.quest.action.party.PartyActionFactory;
import org.betonquest.betonquest.quest.action.point.DeleteGlobalPointActionFactory;
import org.betonquest.betonquest.quest.action.point.DeletePointActionFactory;
import org.betonquest.betonquest.quest.action.point.GlobalPointActionFactory;
import org.betonquest.betonquest.quest.action.point.PointActionFactory;
import org.betonquest.betonquest.quest.action.random.PickRandomActionFactory;
import org.betonquest.betonquest.quest.action.run.RunActionFactory;
import org.betonquest.betonquest.quest.action.run.RunForAllActionFactory;
import org.betonquest.betonquest.quest.action.run.RunIndependentActionFactory;
import org.betonquest.betonquest.quest.action.scoreboard.ScoreboardObjectiveActionFactory;
import org.betonquest.betonquest.quest.action.scoreboard.ScoreboardTagActionFactory;
import org.betonquest.betonquest.quest.action.setblock.SetBlockActionFactory;
import org.betonquest.betonquest.quest.action.spawn.SpawnMobActionFactory;
import org.betonquest.betonquest.quest.action.stage.StageActionFactory;
import org.betonquest.betonquest.quest.action.tag.TagGlobalActionFactory;
import org.betonquest.betonquest.quest.action.tag.TagPlayerActionFactory;
import org.betonquest.betonquest.quest.action.take.TakeActionFactory;
import org.betonquest.betonquest.quest.action.teleport.TeleportActionFactory;
import org.betonquest.betonquest.quest.action.time.TimeActionFactory;
import org.betonquest.betonquest.quest.action.variable.VariableActionFactory;
import org.betonquest.betonquest.quest.action.velocity.VelocityActionFactory;
import org.betonquest.betonquest.quest.action.weather.WeatherActionFactory;
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
import org.betonquest.betonquest.quest.objective.action.ActionObjectiveFactory;
import org.betonquest.betonquest.quest.objective.arrow.ArrowShootObjectiveFactory;
import org.betonquest.betonquest.quest.objective.block.BlockObjectiveFactory;
import org.betonquest.betonquest.quest.objective.breed.BreedObjectiveFactory;
import org.betonquest.betonquest.quest.objective.brew.BrewObjectiveFactory;
import org.betonquest.betonquest.quest.objective.chestput.ChestPutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.command.CommandObjectiveFactory;
import org.betonquest.betonquest.quest.objective.consume.ConsumeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.crafting.CraftingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.data.PointObjectiveFactory;
import org.betonquest.betonquest.quest.objective.data.TagObjectiveFactory;
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
import org.betonquest.betonquest.quest.placeholder.condition.ConditionPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.constant.ConstantPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.eval.EvalPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.item.ItemDurabilityPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.item.ItemPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.location.LocationPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.math.MathPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.name.PlayerNamePlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.name.QuesterPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.npc.NpcPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.objective.ObjectivePropertyPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.point.GlobalPointPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.point.PointPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.random.RandomNumberPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.sync.SyncPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.tag.GlobalTagPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.tag.TagPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.version.VersionPlaceholderFactory;
import org.bukkit.Server;

import java.time.InstantSource;

/**
 * Registers the Conditions, Actions, Objectives and Placeholders that come with BetonQuest.
 */
@SuppressWarnings({"PMD.NcssCount", "PMD.AvoidDuplicateLiterals"})
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
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

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
     * @param betonQuest        the plugin used for primary server access and type registration
     * @param questTypeApi      the Quest Type API
     * @param featureApi        the Feature API
     * @param pluginMessage     the plugin message instance
     * @param placeholders      the {@link Placeholders} to create and resolve placeholders
     * @param globalData        the storage providing global data
     * @param dataStorage       the storage providing player data
     * @param profileProvider   the profile provider instance
     * @param languageProvider  the language provider to get the default language
     * @param playerDataFactory the factory to create player data
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BetonQuest betonQuest,
                          final QuestTypeApi questTypeApi, final FeatureApi featureApi, final PluginMessage pluginMessage,
                          final Placeholders placeholders, final GlobalData globalData,
                          final PlayerDataStorage dataStorage, final ProfileProvider profileProvider,
                          final LanguageProvider languageProvider, final PlayerDataFactory playerDataFactory) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.betonQuest = betonQuest;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.pluginMessage = pluginMessage;
        this.placeholders = placeholders;
        this.globalData = globalData;
        this.dataStorage = dataStorage;
        this.profileProvider = profileProvider;
        this.languageProvider = languageProvider;
        this.playerDataFactory = playerDataFactory;
    }

    /**
     * Registers the Quest Types.
     *
     * @param questTypeRegistries the registry to register the types in
     */
    public void register(final BaseQuestTypeRegistries questTypeRegistries) {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerIdentifier(questTypeRegistries.identifiers());
        registerConditions(questTypeRegistries.condition());
        registerActions(questTypeRegistries.action());
        registerObjectives(questTypeRegistries.objective());
        registerPlaceholders(questTypeRegistries.placeholder());
    }

    private void registerIdentifier(final IdentifierTypeRegistry identifierTypes) {
        final QuestPackageManager packageManager = betonQuest.getQuestPackageManager();
        identifierTypes.register(ActionIdentifier.class, new ActionIdentifierFactory(packageManager));
        identifierTypes.register(ConditionIdentifier.class, new ConditionIdentifierFactory(packageManager));
        identifierTypes.register(ObjectiveIdentifier.class, new ObjectiveIdentifierFactory(packageManager));
        identifierTypes.register(PlaceholderIdentifier.class, new PlaceholderIdentifierFactory(packageManager));
        identifierTypes.register(NpcIdentifier.class, new NpcIdentifierFactory(packageManager));

        identifierTypes.register(ConversationIdentifier.class, new ConversationIdentifierFactory(packageManager));
        identifierTypes.register(ConversationOptionIdentifier.class, new ConversationOptionIdentifierFactory(packageManager));
        identifierTypes.register(ItemIdentifier.class, new ItemIdentifierFactory(packageManager));
        identifierTypes.register(CompassIdentifier.class, new CompassIdentifierFactory(packageManager));
        identifierTypes.register(QuestCancelerIdentifier.class, new QuestCancelerIdentifierFactory(packageManager));
        identifierTypes.register(JournalEntryIdentifier.class, new JournalEntryIdentifierFactory(packageManager));
        identifierTypes.register(JournalMainPageIdentifier.class, new JournalMainPageIdentifierFactory(packageManager));
        identifierTypes.register(MenuIdentifier.class, new MenuIdentifierFactory(packageManager));
        identifierTypes.register(MenuItemIdentifier.class, new MenuItemIdentifierFactory(packageManager));
        identifierTypes.register(ScheduleIdentifier.class, new ScheduleIdentifierFactory(packageManager));
    }

    private void registerConditions(final ConditionTypeRegistry conditionTypes) {
        conditionTypes.register("advancement", new AdvancementConditionFactory(loggerFactory, server));
        conditionTypes.registerCombined("and", new ConjunctionConditionFactory(questTypeApi));
        conditionTypes.register("armor", new ArmorConditionFactory(loggerFactory));
        conditionTypes.register("biome", new BiomeConditionFactory(loggerFactory));
        conditionTypes.register("burning", new BurningConditionFactory(loggerFactory));
        conditionTypes.registerCombined("check", new CheckConditionFactory(betonQuest, placeholders, betonQuest.getQuestPackageManager(), conditionTypes));
        conditionTypes.registerCombined("chestitem", new ChestItemConditionFactory());
        conditionTypes.register("conversation", new ConversationConditionFactory(featureApi.conversationApi()));
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", new EffectConditionFactory(loggerFactory));
        conditionTypes.register("empty", new EmptySlotsConditionFactory(loggerFactory));
        conditionTypes.registerCombined("entities", new EntityConditionFactory());
        conditionTypes.registerCombined("eval", new EvalConditionFactory(betonQuest, placeholders, betonQuest.getQuestPackageManager(), conditionTypes, server.getScheduler(), betonQuest));
        conditionTypes.register("experience", new ExperienceConditionFactory(loggerFactory));
        conditionTypes.register("facing", new FacingConditionFactory(loggerFactory));
        conditionTypes.register("fly", new FlyingConditionFactory(loggerFactory));
        conditionTypes.register("gamemode", new GameModeConditionFactory(loggerFactory));
        conditionTypes.registerCombined("globalpoint", new GlobalPointConditionFactory(betonQuest.getGlobalData()));
        conditionTypes.register("globaltag", new GlobalTagConditionFactory(betonQuest.getGlobalData()));
        conditionTypes.register("hand", new HandConditionFactory(loggerFactory));
        conditionTypes.register("health", new HealthConditionFactory(loggerFactory));
        conditionTypes.register("height", new HeightConditionFactory(loggerFactory));
        conditionTypes.register("hunger", new HungerConditionFactory(loggerFactory));
        conditionTypes.register("inconversation", new InConversationConditionFactory(featureApi.conversationApi()));
        conditionTypes.register("item", new ItemConditionFactory(loggerFactory, dataStorage));
        conditionTypes.register("itemdurability", new ItemDurabilityConditionFactory(loggerFactory));
        conditionTypes.register("journal", new JournalConditionFactory(dataStorage, loggerFactory));
        conditionTypes.register("language", new LanguageConditionFactory(dataStorage, languageProvider, pluginMessage));
        conditionTypes.register("location", new LocationConditionFactory(loggerFactory));
        conditionTypes.register("looking", new LookingAtConditionFactory(loggerFactory));
        conditionTypes.registerCombined("moonphase", new MoonPhaseConditionFactory());
        conditionTypes.register("npcdistance", new NpcDistanceConditionFactory(featureApi, loggerFactory));
        conditionTypes.registerCombined("npclocation", new NpcLocationConditionFactory(featureApi));
        conditionTypes.registerCombined("numbercompare", new NumberCompareConditionFactory());
        conditionTypes.register("objective", new ObjectiveConditionFactory(questTypeApi));
        conditionTypes.registerCombined("or", new AlternativeConditionFactory(questTypeApi));
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.registerCombined("party", new PartyConditionFactory(questTypeApi, profileProvider));
        conditionTypes.register("permission", new PermissionConditionFactory(loggerFactory));
        conditionTypes.register("point", new PointConditionFactory(dataStorage));
        conditionTypes.registerCombined("random", new RandomConditionFactory());
        conditionTypes.register("rating", new ArmorRatingConditionFactory(loggerFactory));
        conditionTypes.register("realtime", new RealTimeConditionFactory());
        conditionTypes.register("ride", new RideConditionFactory(loggerFactory));
        conditionTypes.register("score", new ScoreboardObjectiveConditionFactory());
        conditionTypes.register("scoretag", new ScoreboardTagConditionFactory(loggerFactory));
        conditionTypes.register("sneak", new SneakConditionFactory(loggerFactory));
        conditionTypes.register("stage", new StageConditionFactory(questTypeApi));
        conditionTypes.register("tag", new TagConditionFactory(dataStorage));
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory());
        conditionTypes.registerCombined("time", new TimeConditionFactory());
        conditionTypes.registerCombined("variable", new VariableConditionFactory(loggerFactory));
        conditionTypes.registerCombined("weather", new WeatherConditionFactory());
        conditionTypes.register("world", new WorldConditionFactory(loggerFactory));
    }

    private void registerActions(final ActionTypeRegistry actionTypes) {
        actionTypes.register("burn", new BurnActionFactory(loggerFactory));
        actionTypes.register("cancel", new CancelActionFactory(loggerFactory, featureApi));
        actionTypes.register("cancelconversation", new CancelConversationActionFactory(loggerFactory, featureApi.conversationApi()));
        actionTypes.register("chat", new ChatActionFactory(loggerFactory));
        actionTypes.registerCombined("chestclear", new ChestClearActionFactory());
        actionTypes.registerCombined("chestgive", new ChestGiveActionFactory());
        actionTypes.registerCombined("chesttake", new ChestTakeActionFactory());
        actionTypes.register("compass", new CompassActionFactory(featureApi, dataStorage));
        actionTypes.registerCombined("command", new CommandActionFactory(loggerFactory, server));
        actionTypes.register("conversation", new ConversationActionFactory(loggerFactory, featureApi.conversationApi()));
        actionTypes.register("damage", new DamageActionFactory(loggerFactory));
        actionTypes.register("deleffect", new DeleteEffectActionFactory(loggerFactory));
        actionTypes.registerCombined("deleteglobalpoint", new DeleteGlobalPointActionFactory(globalData));
        actionTypes.registerCombined("deletepoint", new DeletePointActionFactory(dataStorage, betonQuest.getSaver(), profileProvider));
        actionTypes.registerCombined("door", new DoorActionFactory());
        actionTypes.registerCombined("drop", new DropActionFactory(profileProvider));
        actionTypes.register("effect", new EffectActionFactory(loggerFactory));
        actionTypes.registerCombined("eval", new EvalActionFactory(placeholders, betonQuest.getQuestPackageManager(), actionTypes, server.getScheduler(), betonQuest));
        actionTypes.register("experience", new ExperienceActionFactory(loggerFactory));
        actionTypes.registerCombined("explosion", new ExplosionActionFactory());
        actionTypes.registerCombined("folder", new FolderActionFactory(betonQuest, loggerFactory, server.getPluginManager(), questTypeApi));
        actionTypes.registerCombined("first", new FirstActionFactory(questTypeApi));
        actionTypes.register("give", new GiveActionFactory(loggerFactory, dataStorage, pluginMessage));
        actionTypes.register("givejournal", new GiveJournalActionFactory(loggerFactory, dataStorage));
        actionTypes.registerCombined("globaltag", new TagGlobalActionFactory(betonQuest));
        actionTypes.registerCombined("globalpoint", new GlobalPointActionFactory(globalData));
        actionTypes.register("hunger", new HungerActionFactory(loggerFactory));
        actionTypes.registerCombined("if", new IfElseActionFactory(questTypeApi));
        actionTypes.register("itemdurability", new ItemDurabilityActionFactory(loggerFactory));
        actionTypes.registerCombined("journal", new JournalActionFactory(loggerFactory, pluginMessage, dataStorage,
                InstantSource.system(), betonQuest.getSaver(), profileProvider));
        actionTypes.register("kill", new KillActionFactory(loggerFactory));
        actionTypes.register("language", new LanguageActionFactory(dataStorage));
        actionTypes.registerCombined("lever", new LeverActionFactory());
        actionTypes.registerCombined("lightning", new LightningActionFactory());
        actionTypes.registerCombined("log", new LogActionFactory(loggerFactory));
        actionTypes.register("notify", new NotifyActionFactory(loggerFactory, betonQuest.getTextParser(), dataStorage, languageProvider));
        actionTypes.registerCombined("notifyall", new NotifyAllActionFactory(loggerFactory, betonQuest.getTextParser(), dataStorage, profileProvider, languageProvider));
        actionTypes.registerCombined("npcteleport", new NpcTeleportActionFactory(featureApi));
        actionTypes.registerCombined("objective", new ObjectiveActionFactory(betonQuest, loggerFactory, questTypeApi, playerDataFactory));
        actionTypes.register("opsudo", new OpSudoActionFactory(loggerFactory, server));
        actionTypes.register("party", new PartyActionFactory(loggerFactory, questTypeApi, profileProvider));
        actionTypes.registerCombined("pickrandom", new PickRandomActionFactory(questTypeApi));
        actionTypes.register("point", new PointActionFactory(loggerFactory, dataStorage,
                pluginMessage));
        actionTypes.registerCombined("removeentity", new RemoveEntityActionFactory());
        actionTypes.registerCombined("run", new RunActionFactory(betonQuest, placeholders, betonQuest.getQuestPackageManager(), actionTypes));
        actionTypes.register("runForAll", new RunForAllActionFactory(questTypeApi, profileProvider));
        actionTypes.register("runIndependent", new RunIndependentActionFactory(questTypeApi));
        actionTypes.registerCombined("setblock", new SetBlockActionFactory());
        actionTypes.register("score", new ScoreboardObjectiveActionFactory());
        actionTypes.register("scoretag", new ScoreboardTagActionFactory(loggerFactory));
        actionTypes.registerCombined("spawn", new SpawnMobActionFactory());
        actionTypes.register("stage", new StageActionFactory(questTypeApi));
        actionTypes.register("sudo", new SudoActionFactory(loggerFactory, server));
        actionTypes.registerCombined("tag", new TagPlayerActionFactory(dataStorage, betonQuest.getSaver(), profileProvider));
        actionTypes.register("take", new TakeActionFactory(loggerFactory, pluginMessage));
        actionTypes.register("teleport", new TeleportActionFactory(loggerFactory, featureApi.conversationApi()));
        actionTypes.registerCombined("time", new TimeActionFactory());
        actionTypes.register("updatevisibility", new UpdateVisibilityNowActionFactory(featureApi.getNpcHider(), loggerFactory));
        actionTypes.register("variable", new VariableActionFactory(questTypeApi));
        actionTypes.register("velocity", new VelocityActionFactory(loggerFactory));
        actionTypes.registerCombined("weather", new WeatherActionFactory(loggerFactory));
    }

    private void registerObjectives(final FeatureRegistry<ObjectiveFactory> objectiveTypes) {
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
        objectiveTypes.register("point", new PointObjectiveFactory(betonQuest.getPlayerDataStorage()));
        objectiveTypes.register("ride", new RideObjectiveFactory());
        objectiveTypes.register("shear", new ShearObjectiveFactory());
        objectiveTypes.register("smelt", new SmeltingObjectiveFactory());
        objectiveTypes.register("stage", new StageObjectiveFactory());
        objectiveTypes.register("step", new StepObjectiveFactory());
        objectiveTypes.register("tag", new TagObjectiveFactory(betonQuest.getPlayerDataStorage()));
        objectiveTypes.register("tame", new TameObjectiveFactory());
        objectiveTypes.register("timer", new TimerObjectiveFactory(questTypeApi));
        objectiveTypes.register("variable", new VariableObjectiveFactory());
        objectiveTypes.register("equip", new EquipItemObjectiveFactory());
        objectiveTypes.register("jump", new JumpObjectiveFactory());
        objectiveTypes.register("resourcepack", new ResourcepackObjectiveFactory());
    }

    private void registerPlaceholders(final PlaceholderTypeRegistry placeholderTypes) {
        placeholderTypes.register("condition", new ConditionPlaceholderFactory(questTypeApi, pluginMessage));
        placeholderTypes.registerCombined("constant", new ConstantPlaceholderFactory());
        placeholderTypes.registerCombined("eval", new EvalPlaceholderFactory());
        placeholderTypes.register("globalpoint", new GlobalPointPlaceholderFactory(globalData, loggerFactory.create(GlobalPointPlaceholderFactory.class)));
        placeholderTypes.register("globaltag", new GlobalTagPlaceholderFactory(globalData, pluginMessage));
        placeholderTypes.registerCombined("item", new ItemPlaceholderFactory(betonQuest.getPlayerDataStorage()));
        placeholderTypes.register("itemdurability", new ItemDurabilityPlaceholderFactory());
        placeholderTypes.register("location", new LocationPlaceholderFactory());
        placeholderTypes.registerCombined("math", new MathPlaceholderFactory(this.placeholders));
        placeholderTypes.registerCombined("npc", new NpcPlaceholderFactory(featureApi));
        placeholderTypes.register("objective", new ObjectivePropertyPlaceholderFactory(questTypeApi));
        placeholderTypes.register("point", new PointPlaceholderFactory(dataStorage, loggerFactory.create(PointPlaceholderFactory.class)));
        placeholderTypes.register("player", new PlayerNamePlaceholderFactory());
        placeholderTypes.register("quester", new QuesterPlaceholderFactory(featureApi.conversationApi()));
        placeholderTypes.registerCombined("randomnumber", new RandomNumberPlaceholderFactory());
        placeholderTypes.registerCombined("sync", new SyncPlaceholderFactory());
        placeholderTypes.register("tag", new TagPlaceholderFactory(dataStorage, pluginMessage));
        placeholderTypes.register("version", new VersionPlaceholderFactory(betonQuest));
    }
}
