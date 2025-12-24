package org.betonquest.betonquest.quest.objective.stage;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;

import java.util.List;

/**
 * Factory for creating {@link StageObjective} instances from {@link Instruction}s.
 */
public class StageObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the StageObjectiveFactory.
     */
    public StageObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final List<String> stages = instruction.string().getList().getValue(null);
        final StageObjective.StageMap stageMap = new StageObjective.StageMap(stages, (ObjectiveID) instruction.getID());
        final boolean preventCompletion = instruction.hasArgument("preventCompletion");
        return new StageObjective(instruction, stageMap, preventCompletion);
    }
}
