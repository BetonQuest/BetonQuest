package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.Utils;

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
