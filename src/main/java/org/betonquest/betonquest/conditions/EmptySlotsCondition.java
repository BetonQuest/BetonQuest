package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final ItemStack[] items = profile.getOnlineProfile().get().getPlayer().getInventory().getStorageContents();

        int empty = 0;
        for (final ItemStack item : items) {
            if (item == null) {
                empty++;
            }
        }
        return equal ? empty == needed.getInt(profile) : empty >= needed.getInt(profile);
    }
}
