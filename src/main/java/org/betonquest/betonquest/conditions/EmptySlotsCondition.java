package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Checks if the player has required amount of empty slots in his inventory
 */
@SuppressWarnings("PMD.CommentRequired")
public class EmptySlotsCondition extends Condition {

    private final VariableNumber needed;
    private final boolean equal;

    public EmptySlotsCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        needed = instruction.getVarNum();
        equal = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final ItemStack[] items = player.getInventory().getStorageContents();

        int empty = 0;
        for (final ItemStack item : items) {
            if (item == null) {
                empty++;
            }
        }
        return equal ? empty == needed.getInt(playerID) : empty >= needed.getInt(playerID);
    }

}
