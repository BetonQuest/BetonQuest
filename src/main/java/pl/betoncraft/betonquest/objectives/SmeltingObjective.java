package pl.betoncraft.betonquest.objectives;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

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
		material = Material.matchMaterial(instructions.split(" ")[1]);
		amount = Integer.parseInt(instructions.split(" ")[2]);
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@EventHandler
	public void onSmelting(FurnaceExtractEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.equals(PlayerConverter.getPlayer(playerID)) && event.getItemType().equals(material) && checkConditions()) {
			amount = amount - event.getItemAmount();
			if (amount <= 0) {
				HandlerList.unregisterAll(this);
				completeObjective();
			}
		}
	}
	
	@EventHandler
	public void onShiftSmelting(InventoryClickEvent event) {
		if (event.getInventory().getType().equals(InventoryType.FURNACE)) {
			if (event.getRawSlot() == 2) {
				if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
					if (event.getWhoClicked().equals(PlayerConverter.getPlayer(playerID))) {
						event.setCancelled(true);
					}
				}
			} 
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "smelt " + material + " " + amount + " " + conditions + " " + events + " tag:" + tag;
	}

}
