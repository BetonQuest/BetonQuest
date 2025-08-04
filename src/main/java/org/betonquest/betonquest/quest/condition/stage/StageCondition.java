package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.condition.number.Operation;
import org.betonquest.betonquest.quest.objective.stage.StageObjective;

/**
 * The stage condition class to compare the players stage with a given stage.
 */
public class StageCondition implements PlayerCondition {

    /**
     * The stage objective.
     */
    private final Variable<ObjectiveID> objectiveID;

    /**
     * The target stage.
     */
    private final Variable<String> targetStage;

    /**
     * The compare operand between the numbers used for comparing.
     */
    private final Variable<Operation> operation;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates the stage condition.
     *
     * @param questTypeApi the Quest Type API
     * @param objectiveID  the objective ID
     * @param targetStage  the target stage
     * @param operation    the operation
     */
    public StageCondition(final QuestTypeApi questTypeApi, final Variable<ObjectiveID> objectiveID, final Variable<String> targetStage,
                          final Variable<Operation> operation) {
        this.questTypeApi = questTypeApi;
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
        if (stage.getData(profile) == null) {
            return -1.0;
        }
        try {
            return (double) stage.getStageIndex(stage.getStage(profile));
        } catch (final QuestException e) {
            throw new IllegalStateException(profile + " has an invalid stage", e);
        }
    }

    private Double getSecond(final Profile profile) throws QuestException {
        final StageObjective stage = getStageObjective(objectiveID.getValue(profile));
        final String targetState = targetStage.getValue(profile);
        try {
            return (double) stage.getStageIndex(targetState);
        } catch (final QuestException e) {
            throw new IllegalStateException("The stage " + targetState + "' does not exist", e);
        }
    }

    private StageObjective getStageObjective(final ObjectiveID objectiveID) throws QuestException {
        if (questTypeApi.getObjective(objectiveID) instanceof final StageObjective stageObjective) {
            return stageObjective;
        }
        throw new QuestException("Objective '" + objectiveID.getFullID() + "' is not a stage objective");
    }
}
