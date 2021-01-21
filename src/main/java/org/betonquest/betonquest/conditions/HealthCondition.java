package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified amount of health (or more)
 */
@SuppressWarnings("PMD.CommentRequired")
public class HealthCondition extends Condition {

    private final VariableNumber health;

    public HealthCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        health = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return PlayerConverter.getPlayer(playerID).getHealth() >= health.getDouble(playerID);
    }

}
