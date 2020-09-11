package pl.betoncraft.betonquest.conditions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has required amount of empty slots in his inventory
 */
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
