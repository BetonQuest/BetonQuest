package pl.betoncraft.betonquest.objectives;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 */

/**
 * 
 * @author Co0sh
 */
public class SmeltingObjective extends Objective implements Listener {

	private Material material;
	private int amount;
	
	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public SmeltingObjective(String playerID, String instructions) {
		super(playerID, instructions);
		material = Material.getMaterial(instructions.split(" ")[1]);
		amount = Integer.parseInt(instructions.split(" ")[2]);
	}
	
	@EventHandler
	public void onCrafting(FurnaceExtractEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getName().equals(playerID) && event.getItemType().equals(material) && checkConditions()) {
			amount = amount - event.getItemAmount();
			if (amount <= 0) {
				HandlerList.unregisterAll(this);
				completeObjective();
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "smelt " + material + " " + amount + " " + conditions + " " + events + " " + tag;
	}

}
