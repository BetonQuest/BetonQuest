package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * The condition class to compare two numbers.
 */
public class NumberCompareCondition extends BaseNumberCompareCondition {
    /**
     * The number on the left side.
     */
    private final VariableNumber first;

    /**
     * The number of the right side.
     */
    private final VariableNumber second;

    /**
     * The compare operand between the numbers used for comparing.
     */
    private final Operation operation;

    /**
     * Creates the number compare condition.
     *
     * @param instruction instruction to parse
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    public NumberCompareCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.first = instruction.getVarNum();
        this.operation = fromSymbol(instruction.next());
        this.second = instruction.getVarNum();
    }

    @Override
    protected Double getFirst(final Profile profile) throws QuestRuntimeException {
        return first.getValue(profile).doubleValue();
    }

    @Override
    protected Double getSecond(final Profile profile) throws QuestRuntimeException {
        return second.getValue(profile).doubleValue();
    }

    @Override
    protected Operation getOperation() {
        return operation;
    }
}
