package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ObjectiveID;

/**
 * Checks if the player has specified objective active.
 */
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
