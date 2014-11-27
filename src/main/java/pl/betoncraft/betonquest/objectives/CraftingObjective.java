/**
 * 
 */
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.core.Objective;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class CraftingObjective extends Objective implements Listener {
	
	private Material material;
	private byte data = -1;
	private int amount;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public CraftingObjective(String playerID, String instructions) {
		super(playerID, instructions);
		String type = instructions.split(" ")[1];
		if (type.contains(":")) {
			material = Material.getMaterial(type.split(":")[0]);
			data = Byte.parseByte(type.split(":")[1]);
		} else {
			material = Material.getMaterial(type);
		}
		amount = Integer.parseInt(instructions.split(" ")[2]);
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCrafting(CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (player.equals(PlayerConverter.getPlayer(playerID)) && event.getRecipe().getResult().getType().equals(material) && (data < 0 || event.getRecipe().getResult().getData().getData() == data) && checkConditions()) {
				this.amount = amount - event.getRecipe().getResult().getAmount();
				if (amount <= 0) {
					HandlerList.unregisterAll(this);
					completeObjective();
				} else {
				}
			}
		}
	}
	
	@EventHandler
	public void onShiftCrafting(InventoryClickEvent event) {
		if ((event.getInventory().getType().equals(InventoryType.CRAFTING) && event.getRawSlot() == 9) || (event.getInventory().getType().equals(InventoryType.WORKBENCH) && event.getRawSlot() == 8)) {
			if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
				if (event.getWhoClicked().equals(PlayerConverter.getPlayer(playerID))) {
					event.setCancelled(true);
				}
			} 
		}
	}

	@Override
	public String getInstructions() {
		HandlerList.unregisterAll(this);
		return "craft " + material + ":" + data + " " + amount + " " + conditions + " " + events + " tag:" + tag;
	}

}
