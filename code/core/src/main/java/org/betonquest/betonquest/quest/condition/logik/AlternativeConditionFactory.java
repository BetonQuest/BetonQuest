package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;

import java.util.List;

/**
 * Factory for {@link AlternativeCondition}s.
 */
public class AlternativeConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create the alternative condition factory.
     *
     * @param questTypeApi the Quest Type API to check conditions
     */
    public AlternativeConditionFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseAlternative(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseAlternative(instruction));
    }

    private AlternativeCondition parseAlternative(final Instruction instruction) throws QuestException {
        final Argument<List<ConditionID>> conditionIDs = instruction.parse(ConditionID::new).list().get();
        return new AlternativeCondition(questTypeApi, conditionIDs);
    }
}
