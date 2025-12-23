package org.betonquest.betonquest.quest.condition.hunger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

/**
 * Factory for {@link HungerCondition}s.
 */
public class HungerConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for the conditions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the hunger factory.
     *
     * @param loggerFactory the logger factory to create a logger for the conditions
     */
    public HungerConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> hunger = instruction.get(instruction.getParsers().number());
        final BetonQuestLogger log = loggerFactory.create(HungerCondition.class);
        return new OnlineConditionAdapter(new HungerCondition(hunger), log, instruction.getPackage());
    }
}
