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
		event.getPlayer().sendMessage(String.valueOf("1"));
		if (!event.getPlayer().equals(PlayerConverter.getPlayer(playerID))) {
			event.getPlayer().sendMessage(String.valueOf("2"));
			return;
		}
		event.getPlayer().sendMessage(String.valueOf("3"));
		if (type == Material.AIR) {
			event.getPlayer().sendMessage(String.valueOf("4"));
			switch (action) {
			case "right":
				event.getPlayer().sendMessage(String.valueOf("5"));
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions()) {
					event.getPlayer().sendMessage(String.valueOf("6"));
					completeObjective();
					HandlerList.unregisterAll(this);
				}
				break;
			case "left":
				event.getPlayer().sendMessage(String.valueOf("7"));
				if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && checkConditions()) {
					event.getPlayer().sendMessage(String.valueOf("8"));
					completeObjective();
					HandlerList.unregisterAll(this);
				}
				break;
			default:
				event.getPlayer().sendMessage(String.valueOf("9"));
				if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && checkConditions()) {
					event.getPlayer().sendMessage(String.valueOf("0"));
					completeObjective();
					HandlerList.unregisterAll(this);
				}
				break;
			}
		} else {
			event.getPlayer().sendMessage(String.valueOf("A"));
			Action actionEnum;
			switch (action) {
			case "right":
				event.getPlayer().sendMessage(String.valueOf("B"));
				actionEnum = Action.RIGHT_CLICK_BLOCK;
				break;
			case "left":
				event.getPlayer().sendMessage(String.valueOf("C"));
				actionEnum = Action.LEFT_CLICK_BLOCK;
				break;
			default:
				event.getPlayer().sendMessage(String.valueOf("D"));
				actionEnum = null;
				break;
			}
			if (((actionEnum == null && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) || event.getAction().equals(actionEnum)) && (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(type)) && (data < 0 || event.getClickedBlock().getData() == data) && (loc == null || event.getClickedBlock().getLocation().distance(loc) <= range) && checkConditions() ) {
				event.getPlayer().sendMessage(String.valueOf("E"));
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
