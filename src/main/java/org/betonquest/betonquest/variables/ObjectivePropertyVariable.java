package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ObjectiveID;

/**
 * Resolves to a specified property of an objective.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ObjectivePropertyVariable extends Variable {

    private final ObjectiveID objective;
    private final String propertyName;

    public ObjectivePropertyVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        objective = instruction.getObjective();
        propertyName = instruction.next();
    }

    @Override
    public String getValue(final String playerID) {
        final Objective objective = BetonQuest.getInstance().getObjective(this.objective);
        // existence of an objective is checked now because it may not exist yet
        // when variable is created (in case of "message" event)
        if (objective == null) {
            return "";
        }
        return objective.containsPlayer(playerID) ? objective.getProperty(propertyName, playerID) : "";
    }

}
