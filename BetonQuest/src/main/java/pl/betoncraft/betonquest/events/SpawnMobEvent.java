/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * 
 * @author Co0sh
 */
public class SpawnMobEvent extends QuestEvent {
	
	private Location loc;
	private EntityType type;
	private int amount;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public SpawnMobEvent(String playerID, String instructions) {
		super(playerID, instructions);
		loc = decodeLocation(instructions.split(" ")[1]);
		type = EntityType.valueOf(instructions.split(" ")[2]);
		amount = Integer.parseInt(instructions.split(" ")[3]);
		for (int i = 0; i < amount; i++) {
			loc.getWorld().spawnEntity(loc, type);
		}
	}

	/**
	 * @author D¿ejkop
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
