package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for Shopkeepers.
 */
public class ShopkeepersIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public ShopkeepersIntegrator() {

    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        final Plugin shopkeepers = Bukkit.getPluginManager().getPlugin("Shopkeepers");
        final Version shopkeepersVersion = new Version(shopkeepers.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.isOlderThan(shopkeepersVersion, new Version("2.2.0"))) {
            throw new UnsupportedVersionException(shopkeepers, "2.2.0");
        }
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        questRegistries.condition().register("shopamount", new HavingShopConditionFactory());
        questRegistries.event().register("shopkeeper", new OpenShopEventFactory(api.getLoggerFactory()));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
