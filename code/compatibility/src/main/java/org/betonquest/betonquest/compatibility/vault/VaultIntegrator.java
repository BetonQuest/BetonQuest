package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.vault.condition.MoneyConditionFactory;
import org.betonquest.betonquest.compatibility.vault.event.MoneyActionFactory;
import org.betonquest.betonquest.compatibility.vault.event.PermissionActionFactory;
import org.betonquest.betonquest.compatibility.vault.placeholder.MoneyPlaceholderFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * Integrator for <a href="https://github.com/MilkBowl/VaultAPI">Vault</a>.
 */
public class VaultIntegrator implements Integrator {

    /**
     * BetonQuest Plugin for registering.
     */
    private final BetonQuest plugin;

    /**
     * Constructor for the Vault Integration.
     */
    public VaultIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuestLogger log = api.getLoggerFactory().create(VaultIntegrator.class);

        final ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
        final RegisteredServiceProvider<Economy> economyProvider = servicesManager.getRegistration(Economy.class);
        if (economyProvider == null) {
            log.warn("There is no economy plugin on the server!");
        } else {
            final Economy economy = economyProvider.getProvider();
            final QuestTypeRegistries registries = api.getQuestRegistries();

            registries.event().register("money", new MoneyActionFactory(economy, api.getLoggerFactory(),
                    plugin.getPluginMessage()));
            registries.condition().register("money", new MoneyConditionFactory(economy));
            registries.placeholder().register("money", new MoneyPlaceholderFactory(economy));
        }

        final RegisteredServiceProvider<Permission> permissionProvider = servicesManager.getRegistration(Permission.class);
        if (permissionProvider == null) {
            log.warn("Could not get permission provider!");
        } else {
            final Permission permission = permissionProvider.getProvider();
            api.getQuestRegistries().event().register("permission", new PermissionActionFactory(permission));
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
