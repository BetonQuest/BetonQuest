package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * Created by Dzejkop
 */
public class ExplosionEvent extends QuestEvent {

    /**
     * Spawns an explosion in a given location and with given flags
     *
     * Takes instructions in format: setsFireFlag(1 : 0) breaksBlockFlag(1 : 0) power location(x;y;z;world)
     *
     * @param playerID
     * @param instructions
     */
    public ExplosionEvent(String playerID, String instructions) {
        super(playerID, instructions);
		if (!fire) {
			return;
		}

        String [] s = instructions.split(" ");

        boolean setsFire = s[1].equals("1") ? true : false;
        boolean breaksBlocks = s[2].equals("1") ? true : false;

        float power = Float.parseFloat(s[3]);

        Location loc = decodeLocation(s[4]);

        loc.getWorld().createExplosion(
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                power,
                setsFire,
                breaksBlocks
        );

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
