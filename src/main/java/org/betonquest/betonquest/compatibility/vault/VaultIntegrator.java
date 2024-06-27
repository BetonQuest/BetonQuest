package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class VaultIntegrator implements Integrator {
    @SuppressWarnings("NullAway.Init")
    private static VaultIntegrator instance;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final BetonQuest plugin;

    @Nullable
    private Permission permission;

    @Nullable
    private Economy economy;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    public VaultIntegrator() {
        instance = this;
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        plugin = BetonQuest.getInstance();
    }

    public static VaultIntegrator getInstance() {
        return instance;
    }

    /**
     * @return the permission
     */
    @Nullable
    public Permission getPermission() {
        return permission;
    }

    /**
     * @return the economy
     */
    @Nullable
    public Economy getEconomy() {
        return economy;
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
            log.warn("There is no economy plugin on the server!");
        } else {
            plugin.registerEvents("money", MoneyEvent.class);
            plugin.registerConditions("money", MoneyCondition.class);
            plugin.registerVariable("money", MoneyVariable.class);
        }
        if (permission == null) {
            log.warn("Could not get permission provider!");
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
