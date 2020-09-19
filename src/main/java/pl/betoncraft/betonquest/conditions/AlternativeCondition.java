package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;

import java.util.List;

/**
 * One of specified conditions has to be true
 */
public class AlternativeCondition extends Condition {

    private final List<ConditionID> conditions;

    public AlternativeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        conditions = instruction.getList(e -> instruction.getCondition(e));
    }

    @Override
    protected Boolean execute(final String playerID) {
        for (final ConditionID condition : conditions) {
            if (BetonQuest.condition(playerID, condition)) {
                return true;
            }
        }
        return false;
    }
}
