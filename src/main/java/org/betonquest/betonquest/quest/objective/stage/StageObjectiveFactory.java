package org.betonquest.betonquest.quest.objective.stage;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;

/**
 * Factory for creating {@link StageObjective} instances from {@link Instruction}s.
 */
public class StageObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the StageObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public StageObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final List<String> stages = instruction.getList(entry -> entry).getValue(null);
        final StageObjective.StageMap stageMap = new StageObjective.StageMap(stages, (ObjectiveID) instruction.getID());
        final boolean preventCompletion = instruction.hasArgument("preventCompletion");
        final BetonQuestLogger log = loggerFactory.create(StageObjective.class);
        return new StageObjective(instruction, log, stageMap, preventCompletion);
    }
}
