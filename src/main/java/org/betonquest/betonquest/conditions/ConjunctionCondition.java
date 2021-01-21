package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;

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
