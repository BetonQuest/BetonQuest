package org.betonquest.betonquest.quest.condition.party;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;

/**
 * Factory to create party conditions from {@link Instruction}s.
 */
public class PartyConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the party condition factory.
     */
    public PartyConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableLocation location = instruction.getLocation(instruction.getOptional("location", "%location%"));
        return new NullableConditionAdapter(parse(instruction, location));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final VariableLocation location = instruction.getLocation(instruction.getOptional("location"));
        if (location == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        return new NullableConditionAdapter(parse(instruction, location));
    }

    private PartyCondition parse(final Instruction instruction, final VariableLocation location) throws QuestException {
        final VariableNumber range = instruction.getVarNum();
        final ConditionID[] conditions = instruction.getList(instruction::getCondition).toArray(new ConditionID[0]);
        final ConditionID[] everyone = instruction.getList(instruction.getOptional("every"), instruction::getCondition).toArray(new ConditionID[0]);
        final ConditionID[] anyone = instruction.getList(instruction.getOptional("any"), instruction::getCondition).toArray(new ConditionID[0]);
        final VariableNumber count = instruction.getVarNum(instruction.getOptional("count"));

        return new PartyCondition(location, range, conditions, everyone, anyone, count);
    }
}
