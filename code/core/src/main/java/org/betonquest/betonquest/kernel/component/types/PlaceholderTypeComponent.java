package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
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
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading all placeholder types.
 */
public class PlaceholderTypeComponent extends AbstractCoreComponent {

    /**
     * Create a new PlaceholderTypeComponent.
     */
    public PlaceholderTypeComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginMessage.class, GlobalData.class, PlayerDataStorage.class,
                PlaceholderTypeRegistry.class, Conversations.class, ConditionManager.class,
                ObjectiveManager.class, PlaceholderManager.class, NpcManager.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final PlaceholderTypeRegistry placeholderTypes = getDependency(PlaceholderTypeRegistry.class);
        final Translations translations = getDependency(PluginMessage.class);
        final GlobalData globalData = getDependency(GlobalData.class);
        final PlayerDataStorage dataStorage = getDependency(PlayerDataStorage.class);
        final Conversations conversations = getDependency(Conversations.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ObjectiveManager objectiveManager = getDependency(ObjectiveManager.class);
        final PlaceholderManager placeholderManager = getDependency(PlaceholderManager.class);
        final NpcManager npcManager = getDependency(NpcManager.class);

        placeholderTypes.register("condition", new ConditionPlaceholderFactory(conditionManager, translations));
        placeholderTypes.registerCombined("constant", new ConstantPlaceholderFactory());
        placeholderTypes.registerCombined("eval", new EvalPlaceholderFactory());
        placeholderTypes.register("globalpoint", new GlobalPointPlaceholderFactory(globalData));
        placeholderTypes.register("globaltag", new GlobalTagPlaceholderFactory(globalData, translations));
        placeholderTypes.registerCombined("item", new ItemPlaceholderFactory(dataStorage));
        placeholderTypes.register("itemdurability", new ItemDurabilityPlaceholderFactory());
        placeholderTypes.register("location", new LocationPlaceholderFactory());
        placeholderTypes.registerCombined("math", new MathPlaceholderFactory(placeholderManager));
        placeholderTypes.registerCombined("npc", new NpcPlaceholderFactory(conversations, npcManager));
        placeholderTypes.register("objective", new ObjectivePropertyPlaceholderFactory(objectiveManager));
        placeholderTypes.register("point", new PointPlaceholderFactory(dataStorage));
        placeholderTypes.register("player", new PlayerNamePlaceholderFactory());
        placeholderTypes.register("quester", new QuesterPlaceholderFactory(conversations));
        placeholderTypes.registerCombined("randomnumber", new RandomNumberPlaceholderFactory());
        placeholderTypes.registerCombined("sync", new SyncPlaceholderFactory());
        placeholderTypes.register("tag", new TagPlaceholderFactory(dataStorage, translations));
        placeholderTypes.register("version", new VersionPlaceholderFactory(plugin));
    }
}
