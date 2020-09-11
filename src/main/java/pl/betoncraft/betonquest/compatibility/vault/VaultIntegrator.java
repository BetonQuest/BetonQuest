package pl.betoncraft.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;


public class VaultIntegrator implements Integrator {

    private static VaultIntegrator instance;
    private BetonQuest plugin;
    private Permission permission = null;
    private Economy economy = null;

    public VaultIntegrator() {
        instance = this;
        plugin = BetonQuest.getInstance();
    }

    /**
     * @return the permission
     */
    public static Permission getPermission() {
        return instance.permission;
    }

    /**
     * @return the economy
     */
    public static Economy getEconomy() {
        return instance.economy;
    }

    @Override
    public void hook() {
        final RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        if (economy == null) {
            LogUtils.getLogger().log(Level.WARNING, "There is no economy plugin on the server!");
        } else {
            plugin.registerEvents("money", MoneyEvent.class);
            plugin.registerConditions("money", MoneyCondition.class);
            plugin.registerVariable("money", MoneyVariable.class);
        }
        if (permission == null) {
            LogUtils.getLogger().log(Level.WARNING, "Could not get permission provider!");
        } else {
            plugin.registerEvents("permission", PermissionEvent.class);
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
