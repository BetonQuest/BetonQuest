package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * @author Dzejkop
 */
public class TeleportEvent extends QuestEvent {

    /**
     * Constructor method
     * @param playerID
     * @param instructions
     */
    public TeleportEvent(String playerID, String instructions) {
        super(playerID, instructions);
		if (!fire) {
			return;
		}
        Player player = Bukkit.getPlayer(playerID);

        // Ignoring the first part of instruction
        String locationString = instructions.substring(instructions.indexOf(" ")+1);

        // Get the location
        Location loc = decodeLocation(locationString);

        player.teleport(loc);
    }

    /**
     * Parses a location from string
     * @param str
     * @return
     */
    private Location decodeLocation(String str) {

        String [] locArgs = str.split(";");

        Location loc = null;

        if(locArgs.length  == 4) {
            // Location without head alignment
            loc = new Location(
                    Bukkit.getWorld(locArgs[3]),     // World
                    Double.parseDouble(locArgs[0]),  // X
                    Double.parseDouble(locArgs[1]),  // Y
                    Double.parseDouble(locArgs[2])   // Z
                    );
        } else {
            // Location with head alignment
            loc = new Location(
                    Bukkit.getWorld(locArgs[3]),     // World
                    Double.parseDouble(locArgs[0]),  // X
                    Double.parseDouble(locArgs[1]),  // Y
                    Double.parseDouble(locArgs[2]),  // Z
                    Float.parseFloat(locArgs[4]),    // Yaw
                    Float.parseFloat(locArgs[5])     // Pitch
                    );
        }

        return loc;

    }


}
