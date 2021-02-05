package org.betonquest.betonquest.compatibility.vault;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;


@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class VaultIntegrator implements Integrator {

    private static VaultIntegrator instance;
    private final BetonQuest plugin;
    private Permission permission;
    private Economy economy;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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
            LOG.warning(null, "There is no economy plugin on the server!");
        } else {
            plugin.registerEvents("money", MoneyEvent.class);
            plugin.registerConditions("money", MoneyCondition.class);
            plugin.registerVariable("money", MoneyVariable.class);
        }
        if (permission == null) {
            LOG.warning(null, "Could not get permission provider!");
        } else {
            plugin.registerEvents("permission", PermissionEvent.class);
        }
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
