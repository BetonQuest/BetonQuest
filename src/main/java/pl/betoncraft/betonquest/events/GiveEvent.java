/**
 * 
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.core.EffectContainer;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.inout.ConfigInput;
import pl.betoncraft.betonquest.inout.JournalBook;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class GiveEvent extends QuestEvent {
	
	private QuestItem questItem;
	private int amount = 1;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public GiveEvent(String playerID, String instructions) {
		super(playerID, instructions);

		String[] parts = instructions.split(" ");
		questItem = new QuestItem(parts[1]);
		for (String part : parts) {
			if (part.contains("amount:")) {
				amount = Integer.valueOf(part.substring(7));
			}
		}
		while (amount > 0) {
			int stackSize;
			if (amount > 64) {
				stackSize = 64;
			} else {
				stackSize = amount;
			}
			byte data;
			if (questItem.getData() < 0) {
				data = 0;
			} else {
				data = (byte) questItem.getData();
			}
			ItemStack item = new ItemStack(Material.matchMaterial(questItem.getMaterial()), stackSize, data);
			ItemMeta meta = item.getItemMeta();
			if (questItem.getName() != null) {
				meta.setDisplayName(questItem.getName());
			}
			meta.setLore(questItem.getLore());
			for (String enchant : questItem.getEnchants().keySet()) {
				meta.addEnchant(Enchantment.getByName(enchant), questItem.getEnchants().get(enchant), true);
			}
			if (Material.matchMaterial(questItem.getMaterial()).equals(Material.WRITTEN_BOOK)) {
				BookMeta bookMeta = (BookMeta) meta;
				if (questItem.getAuthor() != null) {
					bookMeta.setAuthor(questItem.getAuthor());
				} else {
					bookMeta.setAuthor(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".unknown_author"));
				}
				if (questItem.getText() != null) {
					bookMeta.setPages(JournalBook.pagesFromString(questItem.getText(), false));
				}
				if (questItem.getTitle() != null) {
					bookMeta.setTitle(questItem.getTitle());
				} else {
					bookMeta.setTitle(ConfigInput.getString("messages." + ConfigInput.getString("config.language") + ".unknown_title"));
				}
				item.setItemMeta(bookMeta);
			}
			if (Material.matchMaterial(questItem.getMaterial()).equals(Material.POTION)) {
				PotionMeta potionMeta = (PotionMeta) meta;
				for (EffectContainer effect : questItem.getEffects()) {
					potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect.getType()), effect.getDuration(), effect.getPower()), true);
				}
				item.setItemMeta(potionMeta);
			}
			item.setItemMeta(meta);
			PlayerConverter.getPlayer(playerID).getInventory().addItem(item);
			amount = amount - stackSize;
		}
		
	}

}
