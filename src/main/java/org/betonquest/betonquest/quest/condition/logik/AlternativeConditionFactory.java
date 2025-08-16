package org.betonquest.betonquest.quest.condition.logik;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.List;

/**
 * Factory for {@link AlternativeCondition}s.
 */
public class AlternativeConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Create the alternative condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     * @param questTypeApi  the Quest Type API to check conditions
     */
    public AlternativeConditionFactory(final BetonQuestLoggerFactory loggerFactory, final QuestTypeApi questTypeApi) {
        this.loggerFactory = loggerFactory;
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
        final BetonQuestLogger log = loggerFactory.create(AlternativeCondition.class);
        final Variable<List<ConditionID>> conditionIDs = instruction.getList(ConditionID::new);
        return new AlternativeCondition(log, questTypeApi, conditionIDs, instruction.getPackage());
    }
}
