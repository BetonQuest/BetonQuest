package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory to create {@link DrunkCondition}s from {@link Instruction}s.
 */
public class DrunkConditionFactory implements PlayerConditionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Drunk Conditions.
     *
     * @param loggerFactory the logger factory.
     */
    public DrunkConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> drunkVar = instruction.number().get();
        final BetonQuestLogger logger = loggerFactory.create(DrunkCondition.class);
        return new OnlineConditionAdapter(new DrunkCondition(drunkVar), logger, instruction.getPackage());
    }
}
