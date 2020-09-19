package pl.betoncraft.betonquest.compatibility.shopkeepers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.exceptions.UnsupportedVersionException;


public class ShopkeepersIntegrator implements Integrator {

    private final BetonQuest plugin;

    public ShopkeepersIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws Exception {
        final Plugin shopkeepers = Bukkit.getPluginManager().getPlugin("Shopkeepers");
        if (shopkeepers.getDescription().getVersion().startsWith("1.")) {
            throw new UnsupportedVersionException(shopkeepers, "2.2.0");
        }
        plugin.registerEvents("shopkeeper", OpenShopEvent.class);
        plugin.registerConditions("shopamount", HavingShopCondition.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
