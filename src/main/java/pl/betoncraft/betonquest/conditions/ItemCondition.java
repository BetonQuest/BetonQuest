/**
 * 
 */
package pl.betoncraft.betonquest.conditions;

import org.bukkit.inventory.ItemStack;

import pl.betoncraft.betonquest.core.Condition;
import pl.betoncraft.betonquest.core.QuestItem;
import pl.betoncraft.betonquest.events.TakeEvent;
import pl.betoncraft.betonquest.inout.PlayerConverter;

/**
 * Having item in inventory condition, instrucion string: "item type:DIAMOND_SWORD amount:1 enchants:DAMAGE_ALL:3,KNOCKBACK:1 name:Siekacz --inverted"
 * @author Co0sh
 */
public class ItemCondition extends Condition {
	
	private QuestItem questItem;
	private int amount = 1;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public ItemCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("item:")) {
				questItem = new QuestItem(part.substring(5));
			}
			if (part.contains("amount:")) {
				amount = Integer.valueOf(part.substring(7));
			}
		}
	}

	@Override
	public boolean isMet() {
		ItemStack[] items = PlayerConverter.getPlayer(playerID).getInventory().getContents();
		for (ItemStack item : items) {
			if (item == null) {
				continue;
			}
			if (!TakeEvent.isItemEqual(item, questItem)) {
				continue;
			}
			amount = amount - item.getAmount();
			if (amount <= 0) {
				return true;
			}
		}
		return false;
	}

}
