/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        if (economy != null) {
            plugin.registerEvents("money", MoneyEvent.class);
            plugin.registerConditions("money", MoneyCondition.class);
            plugin.registerVariable("money", MoneyVariable.class);
        } else {
            LogUtils.getLogger().log(Level.WARNING, "There is no economy plugin on the server!");
        }
        if (permission != null) {
            plugin.registerEvents("permission", PermissionEvent.class);
        } else {
            LogUtils.getLogger().log(Level.WARNING, "Could not get permission provider!");
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
