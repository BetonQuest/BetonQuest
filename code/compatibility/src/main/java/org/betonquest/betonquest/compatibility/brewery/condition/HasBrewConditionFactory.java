package org.betonquest.betonquest.compatibility.brewery.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
        final Argument<Number> countVar = instruction.number().atLeast(1).get();
        final Argument<String> nameVar = instruction.string().get();
        final Argument<IdentifierType> mode = instruction.enumeration(IdentifierType.class).get("mode", IdentifierType.NAME);
        final BetonQuestLogger logger = loggerFactory.create(HasBrewCondition.class);
        return new OnlineConditionAdapter(new HasBrewCondition(countVar, nameVar, mode), logger, instruction.getPackage());
    }
}
