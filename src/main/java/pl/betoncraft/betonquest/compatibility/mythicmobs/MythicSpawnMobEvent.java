/**
 * 
 */
package pl.betoncraft.betonquest.compatibility.mythicmobs;

import net.elseland.xikage.MythicMobs.Mobs.MobSpawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * @author co0sh
 *
 */
public class MythicSpawnMobEvent extends QuestEvent {

	private Location loc;
	private String mob;
	private int amount;
	private int level;
	
	/**
	 * @param playerID
	 * @param instructions
	 */
	public MythicSpawnMobEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		loc = decodeLocation(parts[1]);
		mob = parts[2].split(":")[0];
		level = Integer.parseInt(parts[2].split(":")[1]);
		amount = Integer.parseInt(parts[3]);
		for (int i = 0; i < amount; i++) {
			MobSpawner.SpawnMythicMob(mob, loc, level);
		}
	}

	/**
	 * @author Dzejkop
	 * @param locStr
	 * @return
	 */
	private Location decodeLocation(String locStr) {

        String [] coords = locStr.split(";");

        Location loc = new Location(
                Bukkit.getWorld(coords[3]),
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2]));

        return loc;
    }

}
