package pl.betoncraft.betonquest.conditions;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;

import java.util.List;

/**
 * All of specified conditions have to be true
 */
@SuppressWarnings("PMD.CommentRequired")
public class ConjunctionCondition extends Condition {

    private final List<ConditionID> conditions;

    public ConjunctionCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        conditions = instruction.getList(e -> instruction.getCondition(e));
    }

    @Override
    protected Boolean execute(final String playerID) {
        return BetonQuest.conditions(playerID, conditions);
    }
}
