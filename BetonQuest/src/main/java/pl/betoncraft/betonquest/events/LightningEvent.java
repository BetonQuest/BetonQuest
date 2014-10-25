package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * Created by Dzejkop
 */
public class LightningEvent extends QuestEvent {

    /**
     * Spawns a lightning in a given location
     *
     * Takes in instructions in format: location(x;y;z;world)
     *
     * @param playerID
     * @param instructions
     */
    public LightningEvent(String playerID, String instructions) {
        super(playerID, instructions);

        String [] instr = instructions.split(" ");

        Location loc = decodeLocation(instr[1]);

        getWorld(instr[1]).strikeLightning(loc);
    }

    private World getWorld(String locStr) {
        return Bukkit.getWorld(locStr.split(";")[3]);
    }

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
