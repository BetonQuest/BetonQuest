package org.betonquest.betonquest.compatibility.shopkeepers;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.lib.versioning.LegacyVersion;
import org.betonquest.betonquest.lib.versioning.UpdateStrategy;
import org.betonquest.betonquest.lib.versioning.VersionComparator;
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
        final LegacyVersion shopkeepersVersion = new LegacyVersion(shopkeepers.getDescription().getVersion());
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR);
        if (comparator.isOlderThan(shopkeepersVersion, new LegacyVersion("2.2.0"))) {
            throw new UnsupportedVersionException(shopkeepers, "2.2.0");
        }
        api.conditions().registry().register("shopamount", new HavingShopConditionFactory());
        api.actions().registry().register("shopkeeper", new OpenShopActionFactory());
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
