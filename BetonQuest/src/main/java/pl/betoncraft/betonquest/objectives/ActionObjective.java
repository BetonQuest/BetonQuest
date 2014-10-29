/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class ActionObjective extends Objective implements Listener {
	
	private Action action;
	private Material type;
	private byte data;
	private String rawAction;

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
		if (parts[2].contains(":")) {
			type = Material.valueOf(parts[2].split(":")[0]);
			data = Byte.valueOf(parts[2].split(":")[1]);
		} else {
			type = Material.valueOf(parts[2]);
			data = 0;
		}
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			return;
		}
		if ((action == null || event.getAction().equals(action)) && event.getClickedBlock().getType().equals(type) && event.getClickedBlock().getData() == data && checkConditions()) {
			completeObjective();
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "action " + rawAction + " " + type + ":" + data + conditions + " " + events + " tag:" + tag;
	}

}
