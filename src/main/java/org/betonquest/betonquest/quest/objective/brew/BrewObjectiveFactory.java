package org.betonquest.betonquest.quest.objective.brew;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link BrewObjective} instances from {@link Instruction}s.
 */
public class BrewObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Profile provider to get the profile of the player.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new instance of the BrewObjectiveFactory.
     *
     * @param loggerFactory   the logger factory to create a logger for the objectives
     * @param profileProvider the profile provider to get the profile of the player
     */
    public BrewObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final ProfileProvider profileProvider) {
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Item potion = instruction.getItem();
        final Variable<Number> targetAmount = instruction.getVariable(Argument.NUMBER_NOT_LESS_THAN_ZERO);
        final BetonQuestLogger log = loggerFactory.create(BrewObjective.class);
        return new BrewObjective(instruction, targetAmount, log, profileProvider, potion);
    }
}
