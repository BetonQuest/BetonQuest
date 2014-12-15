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
	
	private String action;
	private Material type;
	private byte data = -1;
	private String rawLoc;
	private Location loc = null;
	double range = 0;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ActionObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		action = parts[1];
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
				rawLoc = part;
		        String [] coords = part.substring(4).split(";");
		        loc = new Location(
		                Bukkit.getWorld(coords[3]),
		                Double.parseDouble(coords[0]),
		                Double.parseDouble(coords[1]),
		                Double.parseDouble(coords[2]));
		        range = Double.parseDouble(coords[4]);
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
		if (type == Material.AIR) {
			switch (action) {
			case "right":
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions()) {
					completeObjective();
					HandlerList.unregisterAll(this);
				}
				break;
			case "left":
				if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && checkConditions()) {
					completeObjective();
					HandlerList.unregisterAll(this);
				}
				break;
			default:
				if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions()) {
					completeObjective();
					HandlerList.unregisterAll(this);
				}
				break;
			}
		} else {
			Action actionEnum;
			switch (action) {
			case "right":
				actionEnum = Action.RIGHT_CLICK_BLOCK;
				break;
			case "left":
				actionEnum = Action.LEFT_CLICK_BLOCK;
				break;
			default:
				actionEnum = null;
				break;
			}
			if (((actionEnum == null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) || event.getAction().equals(actionEnum)) && (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(type)) && (data < 0 || event.getClickedBlock().getData() == data) && (loc == null || event.getClickedBlock().getLocation().distance(loc) <= range) && checkConditions() ) {
				completeObjective();
				HandlerList.unregisterAll(this);
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "action " + action + " " + type + ":" + data + " " + rawLoc + " " + conditions + " " + events + " tag:" + tag;
	}

}
