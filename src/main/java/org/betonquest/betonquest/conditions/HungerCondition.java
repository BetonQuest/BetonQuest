package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        return profile.getOnlineProfile().get().getPlayer().getFoodLevel() >= hunger.getValue(profile).doubleValue();
    }

}
