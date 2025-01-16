package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

/**
 * Factory for the {@link ConjunctionCondition} class.
 */
public class ConjunctionConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Constructor for the {@link ConjunctionConditionFactory} class.
     */
    public ConjunctionConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private ConjunctionCondition parse(final Instruction instruction) throws QuestException {
        return new ConjunctionCondition(List.of(instruction.getIDArray(ConditionID::new)));
    }
}
