/**
 * 
 */
package pl.betoncraft.betonquest.compatibility;

import java.util.ArrayList;
import java.util.List;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.citizens.NPCKillObjective;
import pl.betoncraft.betonquest.compatibility.mythicmobs.MythicMobKillObjective;

/**
 * @author co0sh
 *
 */
public class Compatibility {

	private BetonQuest instance = BetonQuest.getInstance();
	private List<String> hooked = new ArrayList<>();

	public Compatibility() {

		// hook into MythicMobs
		try {
			Class.forName("net.elseland.xikage.MythicMobs.API.Events.MythicMobDeathEvent");
			instance.registerObjectives("mmobkill", MythicMobKillObjective.class);
			hooked.add("MythicMobs");
		} catch (ClassNotFoundException e) {
		}

		// hook into Citizens
		try {
			Class.forName("net.citizensnpcs.api.event.NPCDeathEvent");
			instance.registerObjectives("npckill", NPCKillObjective.class);
			hooked.add("Citizens");
		} catch (ClassNotFoundException e) {
		}
		
		// hook into Denizen
		try {
			Class.forName("net.aufdemrand.denizen.scripts.ScriptRegistry");
			hooked.add("Denizen");
		} catch (ClassNotFoundException e) {
		}
		
		// hook into Vault
		try {
			Class.forName("net.milkbowl.vault.permission.Permission");
			hooked.add("Vault");
		} catch (ClassNotFoundException e) {
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

}
