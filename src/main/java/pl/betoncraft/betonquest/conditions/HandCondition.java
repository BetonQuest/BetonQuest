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
 * Having item in inventory condition, instrucion string: "hand type:DIAMOND_SWORD enchants:DAMAGE_ALL:3,KNOCKBACK:1 name:Siekacz"
 * @author Co0sh
 */
public class HandCondition extends Condition {
	
	private QuestItem questItem;

	/**
	 * Constructor method
	 * @param playerID
	 * @param instructions
	 */
	public HandCondition(String playerID, String instructions) {
		super(playerID, instructions);
		String[] parts = instructions.split(" ");
		for (String part : parts) {
			if (part.contains("item:")) {
				questItem = new QuestItem(part.substring(5));
			}
		}
	}

	@Override
	public boolean isMet() {
		ItemStack item = PlayerConverter.getPlayer(playerID).getItemInHand();
		if (TakeEvent.isItemEqual(item, questItem)) {
			return true;
		}
		return false;
	}

}
