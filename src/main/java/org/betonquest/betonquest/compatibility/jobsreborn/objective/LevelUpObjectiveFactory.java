package org.betonquest.betonquest.compatibility.jobsreborn.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory for creating {@link LevelUpObjective} instances from {@link Instruction}s.
 */
public class LevelUpObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the ObjectiveLevelUpEventFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public LevelUpObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableJob job = instruction.get(VariableJob::new);
        final BetonQuestLogger log = loggerFactory.create(LevelUpObjective.class);
        return new LevelUpObjective(instruction, log, job);
    }
}
