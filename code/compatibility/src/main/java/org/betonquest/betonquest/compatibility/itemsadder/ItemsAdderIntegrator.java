package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.itemsadder.action.IAPlayAnimationActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.action.IASetBlockAtActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.condition.IABlockConditionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderItemFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderQuestItemSerializer;
import org.betonquest.betonquest.compatibility.itemsadder.objective.IABlockBreakObjectiveFactory;
import org.betonquest.betonquest.compatibility.itemsadder.objective.IABlockPlaceObjectiveFactory;
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Handles integration with ItemsAdder.
 */
public class ItemsAdderIntegrator implements Integrator {

    /**
     * The prefix of implementations.
     */
    private static final String ITEMS_ADDER = "itemsAdder";

    /**
     * The empty default constructor.
     */
    public ItemsAdderIntegrator() {
        // Empty
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        validateVersion();

        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register(ITEMS_ADDER, new ItemsAdderItemFactory());
        itemRegistry.registerSerializer(ITEMS_ADDER, new ItemsAdderQuestItemSerializer());

        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        final ConditionRegistry condition = questRegistries.condition();
        condition.registerCombined(ITEMS_ADDER + "Block", new IABlockConditionFactory());

        final ActionRegistry action = questRegistries.action();
        action.register(ITEMS_ADDER + "Block", new IASetBlockAtActionFactory());
        action.register(ITEMS_ADDER + "Animation", new IAPlayAnimationActionFactory(api.getLoggerFactory()));

        final ObjectiveRegistry objective = questRegistries.objective();
        objective.register(ITEMS_ADDER + "BlockBreak", new IABlockBreakObjectiveFactory());
        objective.register(ITEMS_ADDER + "BlockPlace", new IABlockPlaceObjectiveFactory());
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }

    /**
     * Aborts the hooking process if the installed version of ItemsAdder is invalid.
     *
     * @throws UnsupportedVersionException if the installed version of ItemsAdder is < 4.0.10.
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin itemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        final Version itemsAdderVersion = new Version(itemsAdder.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.isOlderThan(itemsAdderVersion, new Version("4.0.10"))) {
            throw new UnsupportedVersionException(itemsAdder, "4.0.10+");
        }
    }
}
