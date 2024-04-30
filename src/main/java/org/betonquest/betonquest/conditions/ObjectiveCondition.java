package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ObjectiveID;

/**
 * Checks if the player has specified objective active.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveCondition extends Condition {
    public final ObjectiveID objective;

    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    public ObjectiveCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        objective = instruction.getObjective();
    }

    @Override
    protected Boolean execute(final Profile profile) {
        final Objective objective = BetonQuest.getInstance().getObjective(this.objective);
        if (objective == null) {
            log.debug("Objective " + this.objective + " not found, returning false.");
            return false;
        }
        return objective.containsPlayer(profile);
    }

}
