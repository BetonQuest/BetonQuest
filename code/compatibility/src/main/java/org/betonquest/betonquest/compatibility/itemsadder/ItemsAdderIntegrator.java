package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.itemsadder.action.ItemsAdderPlayAnimationActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.action.ItemsAdderSetBlockActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.action.ItemsAdderSetFurnitureActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderItemFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderQuestItemSerializer;
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
     * The empty default constructor.
     */
    public ItemsAdderIntegrator() {
        // Empty
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        validateVersion();

        final QuestTypeRegistries questTypeRegistries = api.getQuestRegistries();

        questTypeRegistries.action().register("itemsAdderBlockAt", new ItemsAdderSetBlockActionFactory());
        questTypeRegistries.action().register("itemsAdderFurnitureAt", new ItemsAdderSetFurnitureActionFactory());
        questTypeRegistries.action().register("itemsAdderPlayAnimation", new ItemsAdderPlayAnimationActionFactory(api.getLoggerFactory()));

        final ItemRegistry itemRegistry = api.getFeatureRegistries().item();
        itemRegistry.register("itemsAdder", new ItemsAdderItemFactory());
        itemRegistry.registerSerializer("itemsAdder", new ItemsAdderQuestItemSerializer());
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
