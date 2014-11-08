/**
 * 
 */
package pl.betoncraft.betonquest.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.betoncraft.betonquest.core.QuestEvent;

/**
 * 
 * @author Co0sh
 */
public class GiveEvent extends QuestEvent {
	
	private Material type;
	private byte data = 0;
	private int amount = 1;
	private Map<Enchantment,Integer> enchants = new HashMap<Enchantment,Integer>();
	private List<String> lore = new ArrayList<String>();
	private String name;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public GiveEvent(String playerID, String instructions) {
		super(playerID, instructions);

		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("type:")) {
				type = Material.valueOf(part.substring(5));
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
		while (amount > 0) {
			int stackSize;
			if (amount > 64) {
				stackSize = 64;
			} else {
				stackSize = amount;
			}
			ItemStack item = new ItemStack(type, stackSize, data);
			ItemMeta meta = item.getItemMeta();
			if (name != null) {
				meta.setDisplayName(name);
			}
			meta.setLore(lore);
			item.addEnchantments(enchants);
			item.setItemMeta(meta);
			Bukkit.getPlayer(playerID).getInventory().addItem(item);
			amount = amount - stackSize;
		}
		
	}

}
