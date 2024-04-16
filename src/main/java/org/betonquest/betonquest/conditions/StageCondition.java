package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.objectives.StageObjective;

/**
 * The StageCondition class to compare the players stage with a given stage.
 */
public class StageCondition extends BaseNumberCompareCondition {
    /**
     * The stage objective.
     */
    private final ObjectiveID objectiveID;

    /**
     * The target stage.
     */
    private final VariableString targetStage;

    /**
     * The operation.
     */
    private final Operation operation;

    /**
     * Creates the stage condition.
     *
     * @param instruction instruction to parse
     * @throws InstructionParseException when the instruction is invalid
     */
    public StageCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        objectiveID = instruction.getObjective();
        operation = fromSymbol(instruction.next());
        targetStage = new VariableString(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Double getFirst(final Profile profile) throws QuestRuntimeException {
        final StageObjective stage = getStageObjective();
        if (stage.getData(profile) == null) {
            return -1.0;
        }
        try {
            return (double) stage.getStageIndex(stage.getStage(profile));
        } catch (final QuestRuntimeException e) {
            throw new IllegalStateException(profile + " has an invalid stage", e);
        }
    }

    @Override
    protected Double getSecond(final Profile profile) throws QuestRuntimeException {
        final StageObjective stage = getStageObjective();
        final String targetState = targetStage.getString(profile);
        try {
            return (double) stage.getStageIndex(targetState);
        } catch (final QuestRuntimeException e) {
            throw new IllegalStateException("The stage " + targetState + "' does not exist", e);
        }
    }

    @Override
    protected Operation getOperation() {
        return operation;
    }

    private StageObjective getStageObjective() throws QuestRuntimeException {
        if (BetonQuest.getInstance().getObjective(objectiveID) instanceof final StageObjective stageObjective) {
            return stageObjective;
        }
        throw new QuestRuntimeException("Objective '" + objectiveID.getFullID() + "' is not a stage objective");
    }
}
