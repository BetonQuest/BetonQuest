package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

/**
 * Requires the player to have specified amount of hunger (or more)
 */
@SuppressWarnings("PMD.CommentRequired")
public class HungerCondition extends Condition {

    private final VariableNumber hunger;

    public HungerCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        hunger = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        return PlayerConverter.getPlayer(playerID).getFoodLevel() >= hunger.getDouble(playerID);
    }

}
