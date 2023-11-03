package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.objectives.StageObjective;

/**
 * The StageCondition class to compare the players stage with a given stage.
 */
public class StageCondition extends BaseNumberCompareCondition {
    /**
     * The stage objective.
     */
    private final StageObjective stage;

    /**
     * The target stage.
     */
    private final VariableString second;

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
        try {
            stage = (StageObjective) BetonQuest.getInstance().getObjective(instruction.getObjective());
        } catch (final ClassCastException e) {
            throw new InstructionParseException("Objective '" + instruction.getObjective() + "' is not a stage objective", e);
        }
        operation = fromSymbol(instruction.next());
        second = new VariableString(instruction.getPackage(), instruction.next());
    }

    @Override
    protected Double getFirst(final Profile profile) throws QuestRuntimeException {
        return (double) stage.getStageIndex(stage.getStage(profile));
    }

    @Override
    protected Double getSecond(final Profile profile) throws QuestRuntimeException {
        return (double) stage.getStageIndex(second.getString(profile));
    }

    @Override
    protected Operation getOperation() {
        return operation;
    }
}
