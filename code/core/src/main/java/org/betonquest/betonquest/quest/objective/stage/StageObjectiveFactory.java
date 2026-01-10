package org.betonquest.betonquest.quest.objective.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final List<String> stages = instruction.string().list().get().getValue(null);
        final StageObjective.StageMap stageMap = new StageObjective.StageMap(stages, (ObjectiveID) instruction.getID());
        final FlagArgument<Boolean> preventCompletion = instruction.bool().getFlag("preventCompletion", true);
        service.setDefaultData(profile -> stageMap.getStage(0));
        return new StageObjective(service, stageMap, preventCompletion);
    }
}
