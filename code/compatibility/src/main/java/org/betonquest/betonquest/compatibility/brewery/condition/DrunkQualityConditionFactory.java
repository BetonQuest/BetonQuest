package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link DrunkQualityCondition}s from {@link Instruction}s.
 */
public class DrunkQualityConditionFactory implements PlayerConditionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Drunk Quality Conditions.
     *
     * @param loggerFactory the logger factory.
     * @param data          the data used for primary server access.
     */
    public DrunkQualityConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> qualityVar = instruction.get(instruction.getParsers().number());
        final BetonQuestLogger logger = loggerFactory.create(DrunkQualityCondition.class);
        return new PrimaryServerThreadPlayerCondition(
                new OnlineConditionAdapter(new DrunkQualityCondition(qualityVar), logger, instruction.getPackage()), data);
    }
}
