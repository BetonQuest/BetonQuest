package org.betonquest.betonquest.quest.condition.stage;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.objective.StageObjective;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * The stage condition class to compare the players stage with a given stage.
 */
public class StageCondition implements PlayerCondition {

    /**
     * The stage objective.
     */
    private final ObjectiveID objectiveID;

    /**
     * The target stage.
     */
    private final VariableString targetStage;

    /**
     * The compare operand between the numbers used for comparing.
     */
    private final Operation operation;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates the stage condition.
     *
     * @param objectiveID the objective ID
     * @param targetStage the target stage
     * @param operation   the operation
     * @param betonQuest  the BetonQuest instance
     */
    public StageCondition(final ObjectiveID objectiveID, final VariableString targetStage, final Operation operation, final BetonQuest betonQuest) {
        this.objectiveID = objectiveID;
        this.targetStage = targetStage;
        this.operation = operation;
        this.betonQuest = betonQuest;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return operation.check(getFirst(profile), getSecond(profile));
    }

    private Double getFirst(final Profile profile) throws QuestException {
        final StageObjective stage = getStageObjective();
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
        final StageObjective stage = getStageObjective();
        final String targetState = targetStage.getValue(profile);
        try {
            return (double) stage.getStageIndex(targetState);
        } catch (final QuestException e) {
            throw new IllegalStateException("The stage " + targetState + "' does not exist", e);
        }
    }

    private StageObjective getStageObjective() throws QuestException {
        if (betonQuest.getObjective(objectiveID) instanceof final StageObjective stageObjective) {
            return stageObjective;
        }
        throw new QuestException("Objective '" + objectiveID.getFullID() + "' is not a stage objective");
    }
}
