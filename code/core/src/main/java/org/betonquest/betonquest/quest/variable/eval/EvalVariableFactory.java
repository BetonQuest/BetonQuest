package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;

/**
 * A factory for creating Eval variables.
 */
public class EvalVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Create a new Eval variable factory.
     */
    public EvalVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return parseInstruction(instruction);
    }

    /**
     * Parse an instruction into an {@link NullableVariable}.
     *
     * @param instruction the instruction to parse
     * @return the parsed {@link NullableVariable}
     * @throws QuestException if the instruction is invalid
     */
    protected NullableVariable parseNullableVariable(final DefaultInstruction instruction) throws QuestException {
        final String rawInstruction = String.join(".", instruction.getValueParts());
        return new EvalVariable(instruction, instruction.get(rawInstruction, Argument.STRING));
    }

    private NullableVariableAdapter parseInstruction(final DefaultInstruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseNullableVariable(instruction));
    }
}
