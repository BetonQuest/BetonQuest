package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
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
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading all condition types.
 */
@SuppressWarnings("PMD.NcssCount")
public class ConditionTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new ConditionTypesComponent.
     */
    public ConditionTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, Server.class,
                BetonQuestLoggerFactory.class, ProfileProvider.class, GlobalData.class, PlayerDataStorage.class,
                Localizations.class, LanguageProvider.class, Instructions.class,
                ConditionTypeRegistry.class, Conversations.class, ConditionManager.class, ObjectiveManager.class,
                NpcManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final Server server = getDependency(Server.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final GlobalData globalData = getDependency(GlobalData.class);
        final PlayerDataStorage dataStorage = getDependency(PlayerDataStorage.class);
        final Localizations localizations = getDependency(Localizations.class);
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ConditionTypeRegistry conditionTypes = getDependency(ConditionTypeRegistry.class);
        final Conversations conversations = getDependency(Conversations.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ObjectiveManager objectiveManager = getDependency(ObjectiveManager.class);
        final NpcManager npcManager = getDependency(NpcManager.class);

        conditionTypes.register("advancement", new AdvancementConditionFactory(server));
        conditionTypes.registerCombined("and", new ConjunctionConditionFactory(conditionManager));
        conditionTypes.register("armor", new ArmorConditionFactory());
        conditionTypes.register("biome", new BiomeConditionFactory());
        conditionTypes.register("burning", new BurningConditionFactory());
        conditionTypes.registerCombined("check", new CheckConditionFactory(instructions, conditionTypes));
        conditionTypes.registerCombined("chestitem", new ChestItemConditionFactory());
        conditionTypes.register("conversation", new ConversationConditionFactory(conversations));
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", new EffectConditionFactory());
        conditionTypes.register("empty", new EmptySlotsConditionFactory());
        conditionTypes.registerCombined("entities", new EntityConditionFactory());
        conditionTypes.registerCombined("eval", new EvalConditionFactory(instructions, conditionTypes, server.getScheduler(), plugin));
        conditionTypes.register("experience", new ExperienceConditionFactory());
        conditionTypes.register("facing", new FacingConditionFactory());
        conditionTypes.register("fly", new FlyingConditionFactory());
        conditionTypes.register("gamemode", new GameModeConditionFactory());
        conditionTypes.registerCombined("globalpoint", new GlobalPointConditionFactory(globalData));
        conditionTypes.register("globaltag", new GlobalTagConditionFactory(globalData));
        conditionTypes.register("hand", new HandConditionFactory());
        conditionTypes.register("health", new HealthConditionFactory());
        conditionTypes.register("height", new HeightConditionFactory());
        conditionTypes.register("hunger", new HungerConditionFactory());
        conditionTypes.register("inconversation", new InConversationConditionFactory(conversations));
        conditionTypes.register("item", new ItemConditionFactory(dataStorage));
        conditionTypes.register("itemdurability", new ItemDurabilityConditionFactory());
        conditionTypes.register("journal", new JournalConditionFactory(dataStorage));
        conditionTypes.register("language", new LanguageConditionFactory(dataStorage, languageProvider, localizations));
        conditionTypes.register("location", new LocationConditionFactory());
        conditionTypes.register("looking", new LookingAtConditionFactory());
        conditionTypes.registerCombined("moonphase", new MoonPhaseConditionFactory());
        conditionTypes.register("npcdistance", new NpcDistanceConditionFactory(npcManager));
        conditionTypes.registerCombined("npclocation", new NpcLocationConditionFactory(npcManager));
        conditionTypes.registerCombined("numbercompare", new NumberCompareConditionFactory());
        conditionTypes.register("objective", new ObjectiveConditionFactory(objectiveManager));
        conditionTypes.registerCombined("or", new AlternativeConditionFactory(conditionManager));
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.registerCombined("party", new PartyConditionFactory(conditionManager, profileProvider));
        conditionTypes.register("permission", new PermissionConditionFactory());
        conditionTypes.register("point", new PointConditionFactory(dataStorage));
        conditionTypes.registerCombined("random", new RandomConditionFactory());
        conditionTypes.register("rating", new ArmorRatingConditionFactory());
        conditionTypes.register("realtime", new RealTimeConditionFactory());
        conditionTypes.register("ride", new RideConditionFactory());
        conditionTypes.register("score", new ScoreboardObjectiveConditionFactory());
        conditionTypes.register("scoretag", new ScoreboardTagConditionFactory());
        conditionTypes.register("sneak", new SneakConditionFactory());
        conditionTypes.register("stage", new StageConditionFactory(objectiveManager));
        conditionTypes.register("tag", new TagConditionFactory(dataStorage));
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory());
        conditionTypes.registerCombined("time", new TimeConditionFactory());
        conditionTypes.registerCombined("variable", new VariableConditionFactory(loggerFactory));
        conditionTypes.registerCombined("weather", new WeatherConditionFactory());
        conditionTypes.register("world", new WorldConditionFactory());
    }
}
