package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.quest.condition.number.Operation;
import org.betonquest.betonquest.quest.objective.stage.StageObjective;

/**
 * The stage condition class to compare the players stage with a given stage.
 */
public class StageCondition implements PlayerCondition {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The stage objective.
     */
    private final Argument<ObjectiveIdentifier> objectiveID;

    /**
     * The target stage.
     */
    private final Argument<String> targetStage;

    /**
     * The compare operand between the numbers used for comparing.
     */
    private final Argument<Operation> operation;

    /**
     * Creates the stage condition.
     *
     * @param objectiveManager the objective manager
     * @param objectiveID      the objective ID
     * @param targetStage      the target stage
     * @param operation        the operation
     */
    public StageCondition(final ObjectiveManager objectiveManager, final Argument<ObjectiveIdentifier> objectiveID,
                          final Argument<String> targetStage, final Argument<Operation> operation) {
        this.objectiveManager = objectiveManager;
        this.objectiveID = objectiveID;
        this.targetStage = targetStage;
        this.operation = operation;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return operation.getValue(profile).check(getFirst(profile), getSecond(profile));
    }

    private Double getFirst(final Profile profile) throws QuestException {
        final StageObjective stage = getStageObjective(objectiveID.getValue(profile));
        if (stage.getService().getData().get(profile) == null) {
            return -1.0;
        }
        return (double) stage.getStageIndex(stage.getStage(profile));
    }

    private Double getSecond(final Profile profile) throws QuestException {
        final StageObjective stage = getStageObjective(objectiveID.getValue(profile));
        final String targetState = targetStage.getValue(profile);
        return (double) stage.getStageIndex(targetState);
    }

    private StageObjective getStageObjective(final ObjectiveIdentifier objectiveID) throws QuestException {
        if (objectiveManager.getObjective(objectiveID) instanceof final StageObjective stageObjective) {
            return stageObjective;
        }
        throw new QuestException("Objective '" + objectiveID + "' is not a stage objective");
    }
}
