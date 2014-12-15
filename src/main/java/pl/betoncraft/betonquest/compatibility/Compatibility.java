/**
 * 
 */
package pl.betoncraft.betonquest.compatibility;

import java.util.ArrayList;
import java.util.List;

import pl.betoncraft.betonquest.BetonQuest;
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
		} catch (ClassNotFoundException e) {}
		
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
