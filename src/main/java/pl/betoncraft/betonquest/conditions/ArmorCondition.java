package pl.betoncraft.betonquest.conditions;

import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to wear this item as an armor
 */
public class ArmorCondition extends Condition {

    private final QuestItem item;

    public ArmorCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        item = new QuestItem(instruction.getItem());
    }

    @Override
    protected Boolean execute(final String playerID) {
        for (final ItemStack armor : PlayerConverter.getPlayer(playerID).getEquipment().getArmorContents()) {
            if (item != null && item.compare(armor)) {
                return true;
            }
        }
        return false;
    }

}
