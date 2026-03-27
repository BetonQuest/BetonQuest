package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.vault.action.MoneyActionFactory;
import org.betonquest.betonquest.compatibility.vault.action.PermissionActionFactory;
import org.betonquest.betonquest.compatibility.vault.condition.MoneyConditionFactory;
import org.betonquest.betonquest.compatibility.vault.placeholder.MoneyPlaceholderFactory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * Integrator for <a href="https://github.com/MilkBowl/VaultAPI">Vault</a>.
 */
public class VaultIntegrator implements Integration {

    /**
     * Bukkit services manager.
     */
    private final ServicesManager servicesManager;

    /**
     * Constructor for the Vault Integration.
     *
     * @param servicesManager the Bukkit services manager
     */
    public VaultIntegrator(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        final BetonQuestLogger log = api.loggerFactory().create(VaultIntegrator.class);

        final RegisteredServiceProvider<Economy> economyProvider = servicesManager.getRegistration(Economy.class);
        if (economyProvider == null) {
            log.warn("There is no economy plugin on the server!");
        } else {
            final Economy economy = economyProvider.getProvider();

            api.actions().registry().register("money", new MoneyActionFactory(economy, api.loggerFactory(),
                    BetonQuest.getInstance().getPluginMessage()));
            api.conditions().registry().register("money", new MoneyConditionFactory(economy));
            api.placeholders().registry().register("money", new MoneyPlaceholderFactory(economy));
        }

        final RegisteredServiceProvider<Permission> permissionProvider = servicesManager.getRegistration(Permission.class);
        if (permissionProvider == null) {
            log.warn("Could not get permission provider!");
        } else {
            final Permission permission = permissionProvider.getProvider();
            api.actions().registry().register("permission", new PermissionActionFactory(permission));
        }
    }

    @Override
    public void disable() {
        // Empty
    }
}
