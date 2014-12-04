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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.betoncraft.betonquest.core.EffectContainer;
import pl.betoncraft.betonquest.core.QuestEvent;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.inout.JournalBook;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * 
 * @author Co0sh
 */
public class TakeEvent extends QuestEvent {

	private QuestItem questItem;
	private int amount = 1;
	
	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public TakeEvent(String playerID, String instructions) {
		super(playerID, instructions);
		
		String[] parts = instructions.split(" ");
		questItem = new QuestItem(parts[1]);
		for (String part : parts) {
			if (part.contains("amount:")) {
				amount = Integer.valueOf(part.substring(7));
			}
		}
		ItemStack[] items = PlayerConverter.getPlayer(playerID).getInventory().getContents();
		for (ItemStack item : items) {
			if (isItemEqual(item, questItem)) {
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
	
	@SuppressWarnings("deprecation")
	public static boolean isItemEqual(ItemStack item, QuestItem questItem) {
		if (item == null) {
			return false;
		}
		if (item.getType() != Material.matchMaterial(questItem.getMaterial())) {
			return false;
		}
		if (questItem.getData() >= 0 && item.getData().getData() != questItem.getData()) {
			return false;
		}
		if (questItem.getName() != null && (!item.getItemMeta().hasDisplayName() || !item.getItemMeta().getDisplayName().equals(questItem.getName()))) {
			return false;
		}
		if (!questItem.getLore().isEmpty() && (!item.getItemMeta().hasLore() || !item.getItemMeta().getLore().equals(questItem.getLore()))) {
			return false;
		}
		if (!questItem.getEnchants().isEmpty()) {
			Map<Enchantment,Integer> enchants = new HashMap<>();
			for (String enchant : questItem.getEnchants().keySet()) {
				enchants.put(Enchantment.getByName(enchant), questItem.getEnchants().get(enchant));
			}
			if (!item.getEnchantments().equals(enchants)) {
				return false;
			}
		}
		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta bookMeta = (BookMeta) item.getItemMeta();
			if (questItem.getAuthor() != null && (!bookMeta.hasAuthor() || !bookMeta.getAuthor().equals(questItem.getAuthor()))) {
				return false;
			}
			if (!questItem.getLore().isEmpty() && (!bookMeta.hasLore() || !bookMeta.getLore().equals(questItem.getLore()))) {
				return false;
			}
			if (questItem.getText() != null && (!bookMeta.hasPages() || !bookMeta.getPages().equals(JournalBook.pagesFromString(questItem.getText())))) {
				return false;
			}
		} else if (item.getType().equals(Material.POTION)) {
			PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
			List<PotionEffect> effects = new ArrayList<>();
			for (EffectContainer effect : questItem.getEffects()) {
				effects.add(new PotionEffect(PotionEffectType.getByName(effect.getType()), effect.getDuration(), effect.getPower()));
			}
			if (!questItem.getEffects().isEmpty() && (!potionMeta.hasCustomEffects() || !potionMeta.getCustomEffects().equals(effects))) {
				return false;
			}
		}
		return true;
	}

}
