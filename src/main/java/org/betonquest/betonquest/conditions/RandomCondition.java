package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.Random;

/**
 * The condition that is met randomly.
 */
@SuppressWarnings("PMD.CommentRequired")
public class RandomCondition extends Condition {

    private final Random random = new Random();
    private final VariableNumber valueMax;
    private final VariableNumber rangeOfRandom;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public RandomCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        staticness = true;
        persistent = true;
        final String[] values = instruction.next().split("-");
        if (values.length != 2) {
            throw new InstructionParseException("Wrong randomness format");
        }
        final QuestPackage pack = instruction.getPackage();
        try {
            valueMax = new VariableNumber(pack, values[0]);
            rangeOfRandom = new VariableNumber(pack, values[1]);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Cannot parse randomness values", e);
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final int temp = random.nextInt(rangeOfRandom.getInt(profile)) + 1;
        return temp <= valueMax.getInt(profile);
    }

}
