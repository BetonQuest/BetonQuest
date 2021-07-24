package pl.betoncraft.betonquest.compatibility.shopkeepers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.exceptions.HookException;
import pl.betoncraft.betonquest.exceptions.UnsupportedVersionException;


@SuppressWarnings("PMD.CommentRequired")
public class ShopkeepersIntegrator implements Integrator {

    private final BetonQuest plugin;

    public ShopkeepersIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void hook() throws HookException {
        final Plugin shopkeepers = Bukkit.getPluginManager().getPlugin("Shopkeepers");
        final String[] versionParts = shopkeepers.getDescription().getVersion().split("\\.");
        try {
            final int part1 = Integer.parseInt(versionParts[0]);
            final int part2 = Integer.parseInt(versionParts[1]);
            if (part1 < 2 || part1 == 2 && part2 < 2) {
                throw new UnsupportedVersionException(shopkeepers, "2.2.0");
            }
        } catch (final NumberFormatException e) {
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
