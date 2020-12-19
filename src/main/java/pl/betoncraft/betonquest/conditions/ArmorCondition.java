package pl.betoncraft.betonquest.conditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to wear this item as an armor
 */
@SuppressWarnings("PMD.CommentRequired")
public class ArmorCondition extends Condition {

    private final QuestItem item;

    public ArmorCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        item = new QuestItem(instruction.getItem());
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Boolean execute(final String playerID) {
        for (final ItemStack armor : PlayerConverter.getPlayer(playerID).getEquipment().getArmorContents()) {
            if (item != null && item.compare(armor)) {
                return true;
            }
        }
        return false;
    }

}
