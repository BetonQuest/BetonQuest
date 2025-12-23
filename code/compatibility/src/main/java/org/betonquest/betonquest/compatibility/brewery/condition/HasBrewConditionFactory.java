package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;

/**
 * Factory to create {@link HasBrewCondition}s from {@link Instruction}s.
 */
public class HasBrewConditionFactory implements PlayerConditionFactory {

    /**
     * The logger factory.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Has Brew Conditions.
     *
     * @param loggerFactory the logger factory.
     */
    public HasBrewConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> countVar = instruction.get(instruction.getParsers().number().atLeast(1));
        final Variable<String> nameVar = instruction.get(instruction.getParsers().string());
        final Variable<IdentifierType> mode = instruction.getValue("mode", instruction.getParsers().forEnum(IdentifierType.class), IdentifierType.NAME);
        final BetonQuestLogger logger = loggerFactory.create(HasBrewCondition.class);
        return new OnlineConditionAdapter(new HasBrewCondition(countVar, nameVar, mode), logger, instruction.getPackage());
    }
}
