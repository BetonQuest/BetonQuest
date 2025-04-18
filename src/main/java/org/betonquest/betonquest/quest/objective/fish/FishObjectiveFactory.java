package org.betonquest.betonquest.quest.objective.fish;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;

/**
 * Factory for creating {@link FishObjective} instances from {@link Instruction}s.
 */
public class FishObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the FishObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public FishObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final QuestItem questItem = new QuestItem(instruction.getID(ItemID::new));
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);

        final String loc = instruction.getOptional("hookLocation");
        final String range = instruction.getOptional("range");
        final boolean hookIsNotNull = loc != null && range != null;
        final VariableLocation hookTargetLocation = hookIsNotNull ? instruction.get(loc, VariableLocation::new) : null;
        final VariableNumber rangeVar = hookIsNotNull ? instruction.get(range, VariableNumber::new) : null;
        final BetonQuestLogger log = loggerFactory.create(FishObjective.class);
        return new FishObjective(instruction, targetAmount, log, questItem, hookTargetLocation, rangeVar);
    }
}
