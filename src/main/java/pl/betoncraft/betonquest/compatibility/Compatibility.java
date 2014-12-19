/**
 * 
 */
package pl.betoncraft.betonquest.compatibility;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.citizens.CitizensListener;
import pl.betoncraft.betonquest.compatibility.citizens.NPCKillObjective;
import pl.betoncraft.betonquest.compatibility.mythicmobs.MythicMobKillObjective;
import pl.betoncraft.betonquest.compatibility.mythicmobs.MythicSpawnMobEvent;
import pl.betoncraft.betonquest.compatibility.vault.MoneyCondition;
import pl.betoncraft.betonquest.compatibility.vault.MoneyEvent;
import pl.betoncraft.betonquest.compatibility.vault.PermissionEvent;

/**
 * @author co0sh
 *
 */
public class Compatibility {

	private BetonQuest instance = BetonQuest.getInstance();
	private List<String> hooked = new ArrayList<>();

	private static Permission permission = null;
    private static Economy economy = null;

	public Compatibility() {

		// hook into MythicMobs
		if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
			instance.registerObjectives("mmobkill", MythicMobKillObjective.class);
			instance.registerEvents("mspawnmob", MythicSpawnMobEvent.class);
			hooked.add("MythicMobs");
		}

		// hook into Citizens
		if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
			new CitizensListener();
			instance.registerObjectives("npckill", NPCKillObjective.class);
			hooked.add("Citizens");
		}
		
		// hook into Vault
		if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
		    RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		    if (permissionProvider != null) {
		        permission = permissionProvider.getProvider();
		    }
		    RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		    if (economyProvider != null) {
		        economy = economyProvider.getProvider();
		    }
		    instance.registerEvents("money", MoneyEvent.class);
		    instance.registerConditions("money", MoneyCondition.class);
		    instance.registerEvents("permission", PermissionEvent.class);
			hooked.add("Vault");
		}

		// log which plugins have been hooked
		if (hooked.size() > 0) {
			StringBuilder string = new StringBuilder();
			for (String plugin : hooked) {
				string.append(plugin + ", ");
			}
			String plugins = string.substring(0, string.length() - 2);
			instance.getLogger().info("Hooked into " + plugins + "!");
		}
	}

	/**
	 * @return the permission
	 */
	public static Permission getPermission() {
		return permission;
	}

	/**
	 * @return the economy
	 */
	public static Economy getEconomy() {
		return economy;
	}

}
