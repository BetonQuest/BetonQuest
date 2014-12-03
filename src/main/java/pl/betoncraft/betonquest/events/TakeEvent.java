/**
 * 
 */
package pl.betoncraft.betonquest.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class TakeEvent extends QuestEvent {

	private Material type;
	private byte data = -1;
	private int amount = 1;
	private Map<Enchantment,Integer> enchants = new HashMap<Enchantment,Integer>();
	private List<String> lore = new ArrayList<String>();
	private String name;
	
	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	@SuppressWarnings("deprecation")
	public TakeEvent(String playerID, String instructions) {
		super(playerID, instructions);
		
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("type:")) {
				type = Material.matchMaterial(part.substring(5));
			} else if (part.contains("data:")) {
				data = Byte.valueOf(part.substring(5));
			} else if (part.contains("amount:")) {
				amount = Integer.valueOf(part.substring(7));
			} else if (part.contains("enchants:")) {
				for (String enchant : part.substring(9).split(",")) {
					enchants.put(Enchantment.getByName(enchant.split(":")[0]), Integer.decode(enchant.split(":")[1]));
				}
			} else if (part.contains("lore:")) {
				for (String loreLine : part.substring(5).split(";")) {
					lore.add(loreLine.replaceAll("_", " "));
				}
			} else if (part.contains("name:")) {
				name = part.substring(5).replaceAll("_", " ");
			}
		}
		ItemStack[] items = PlayerConverter.getPlayer(playerID).getInventory().getContents();
		for (ItemStack item : items) {
			if (item != null && item.getType().equals(type) && (data < 0 || item.getData().getData() == data) && (name == null || (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name))) && (lore.isEmpty() || (item.getItemMeta().hasLore() && item.getItemMeta().getLore().equals(lore))) && (enchants.isEmpty() || (item.getEnchantments().equals(enchants)))) {
				if (item.getAmount() - amount <= 0) {
					amount = amount - item.getAmount();
					item.setType(Material.AIR);
				} else {
					item.setAmount(item.getAmount() - amount);
					amount = 0;
				}
				if (amount <= 0) {
					break;
				}
			}
		}
		PlayerConverter.getPlayer(playerID).getInventory().setContents(items);
	}

}
