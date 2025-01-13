package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.vault.condition.MoneyConditionFactory;
import org.betonquest.betonquest.compatibility.vault.event.MoneyEventFactory;
import org.betonquest.betonquest.compatibility.vault.event.PermissionEventFactory;
import org.betonquest.betonquest.compatibility.vault.variable.MoneyVariableFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * Integrator for <a href="https://github.com/MilkBowl/VaultAPI">Vault</a>.
 */
public class VaultIntegrator implements Integrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * BetonQuest Plugin for registering.
     */
    private final BetonQuest plugin;

    /**
     * Constructor for the Vault Integration.
     */
    public VaultIntegrator() {
        plugin = BetonQuest.getInstance();
        this.log = plugin.getLoggerFactory().create(getClass());
    }

    @Override
    public void hook() {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
        final RegisteredServiceProvider<Economy> economyProvider = servicesManager.getRegistration(Economy.class);
        if (economyProvider == null) {
            log.warn("There is no economy plugin on the server!");
        } else {
            final Economy economy = economyProvider.getProvider();
            final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
            final QuestTypeRegistries registries = plugin.getQuestRegistries();

            registries.getEventTypes().register("money", new MoneyEventFactory(economy, loggerFactory, data, plugin.getVariableProcessor()));
            registries.getConditionTypes().register("money", new MoneyConditionFactory(economy, data));
            registries.getVariableTypes().register("money", new MoneyVariableFactory(economy));
        }

        final RegisteredServiceProvider<Permission> permissionProvider = servicesManager.getRegistration(Permission.class);
        if (permissionProvider == null) {
            log.warn("Could not get permission provider!");
        } else {
            final Permission permission = permissionProvider.getProvider();
            plugin.getQuestRegistries().getEventTypes().register("permission", new PermissionEventFactory(permission, data));
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
