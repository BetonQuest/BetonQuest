package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
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
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return BetonQuest.getInstance().getObjective(objective).containsPlayer(profile);
    }

}
