package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ObjectiveID;

/**
 * Checks if the player has specified objective active.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveCondition extends Condition {

    public final ObjectiveID objective;

    public ObjectiveCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        objective = instruction.getObjective();
        if (objective == null) {
            throw new InstructionParseException("Objective does not exist");
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        return BetonQuest.getInstance().getObjective(objective).containsPlayer(playerID);
    }

}
