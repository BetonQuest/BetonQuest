package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ObjectiveID;

/**
 * Resolves to a specified property of an objective.
 */
public class ObjectivePropertyVariable extends Variable {

    private ObjectiveID objective;
    private String propertyName;

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
