/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import pl.betoncraft.betonquest.core.Objective;

/**
 * 
 * @author Co0sh
 */
public class CraftingObjective extends Objective implements Listener {
	
	private Material material;
	private byte data;
	private int amount;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public CraftingObjective(String playerID, String instructions) {
		super(playerID, instructions);
		material = Material.getMaterial(instructions.split(" ")[1].split(":")[1]);
		data = Byte.parseByte(instructions.split(" ")[1].split(":")[2]);
		amount = Integer.parseInt(instructions.split(" ")[2]);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCrafting(CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (player.getName().equals(playerID) && event.getCursor().getType().equals(material) && event.getCursor().getData().getData() == data && checkConditions()) {
				HandlerList.unregisterAll(this);
				completeObjective();
			}
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "craft " + material + ":" + data + " " + amount + " " + conditions + " " + events + " " + tag;
	}

}
