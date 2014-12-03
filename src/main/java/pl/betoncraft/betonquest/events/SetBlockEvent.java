package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import pl.betoncraft.betonquest.core.QuestEvent;

public class SetBlockEvent extends QuestEvent {
	
	private Material block;
	private byte data = 0;
	private Location loc;

	@SuppressWarnings("deprecation")
	public SetBlockEvent(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("block:")) {
				block = Material.matchMaterial(part.substring(6));
			}
			if (part.contains("data:")) {
				data = Byte.parseByte(part.substring(5));
			}
			if (part.contains("loc:")) {
				loc = decodeLocation(part.substring(4));
			}
		}
		if (block != null && loc != null) {
			loc.getBlock().setType(block);
			loc.getBlock().setData(data);
		}
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
