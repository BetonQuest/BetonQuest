package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Pointer;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Checks if the player has specified pointer in his journal
 */
@SuppressWarnings("PMD.CommentRequired")
public class JournalCondition extends Condition {

    private final String targetPointer;

    public JournalCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        targetPointer = Utils.addPackage(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Boolean execute(final String playerID) {
        for (final Pointer pointer : BetonQuest.getInstance().getPlayerData(playerID).getJournal().getPointers()) {
            if (pointer.getPointer().equalsIgnoreCase(targetPointer)) {
                return true;
            }
        }
        return false;
    }
}
