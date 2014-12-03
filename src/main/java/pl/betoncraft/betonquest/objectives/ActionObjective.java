/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class ActionObjective extends Objective implements Listener {
	
	private Action action;
	private Material type;
	private byte data = -1;
	private String rawAction;
	private Location loc;
	double range = 0;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ActionObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		rawAction = parts[1];
		switch (parts[1]) {
		case "right":
			action = Action.RIGHT_CLICK_BLOCK;
			break;
		case "left":
			action = Action.LEFT_CLICK_BLOCK;
			break;
		default:
			action = null;
			break;
		}
		if (parts[2].equalsIgnoreCase("any")) {
			type = Material.AIR;
		} else {
			if (parts[2].contains(":")) {
				type = Material.matchMaterial(parts[2].split(":")[0]);
				data = Byte.valueOf(parts[2].split(":")[1]);
			} else {
				type = Material.matchMaterial(parts[2]);
			}
		}
		for (String part : parts) {
			if (part.contains("loc:")) {
		        String [] coords = part.substring(4).split(";");
		        loc = new Location(
		                Bukkit.getWorld(coords[3]),
		                Double.parseDouble(coords[0]),
		                Double.parseDouble(coords[1]),
		                Double.parseDouble(coords[2]));
		        range = Double.parseDouble(coords[3]);
			}
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.getPlayer().equals(PlayerConverter.getPlayer(playerID))) {
			return;
		}
		if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			return;
		}
		if ((action == null || event.getAction().equals(action)) && (type.equals(Material.AIR) || event.getClickedBlock().getType().equals(type)) && (data < 0 || event.getClickedBlock().getData() == data) && checkConditions()) {
			if (loc == null || event.getClickedBlock().getLocation().distance(loc) <= range) {
				HandlerList.unregisterAll(this);
				completeObjective();
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "action " + rawAction + " " + type + ":" + data + " " + conditions + " " + events + " tag:" + tag;
	}

}
