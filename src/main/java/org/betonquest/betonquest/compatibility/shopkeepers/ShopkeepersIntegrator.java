package org.betonquest.betonquest.compatibility.shopkeepers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;


@SuppressWarnings("PMD.CommentRequired")
public class ShopkeepersIntegrator implements Integrator {

    private final BetonQuest plugin;

    public ShopkeepersIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void hook() throws HookException {
        final Plugin shopkeepers = Bukkit.getPluginManager().getPlugin("Shopkeepers");
        if (shopkeepers.getDescription().getVersion().startsWith("1.")) {
            throw new UnsupportedVersionException(shopkeepers, "2.2.0");
        }
        plugin.registerEvents("shopkeeper", OpenShopEvent.class);
        plugin.registerConditions("shopamount", HavingShopCondition.class);
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
