package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestItemCondition extends Condition {

	private final Instruction.Item[] questItems;

	public EnderChestItemCondition(final Instruction instruction) throws InstructionParseException {
		super(instruction, true);
		questItems = instruction.getItemList();
	}

	@Override
	protected Boolean execute(final String playerID) throws QuestRuntimeException {
		//get player enderchest contents
		ItemStack[] enderchest = PlayerConverter.getPlayer(playerID).getEnderChest().getContents();
			for (int i = 0; i < questItems.length; i++) {	 //search for items
				int amount = questItems[i].getAmount().getInt(playerID);
				for (int j = 0; j < enderchest.length; j++) {
					ItemStack item = enderchest[j];
					if (item != null && questItems[i].isItemEqual(item)) {
						amount -= item.getAmount();
						if (amount <= 0) {
							//found item, stop counting
							return true;
						}
					}
				}
			}
		//all items
		return false;
	}

}
