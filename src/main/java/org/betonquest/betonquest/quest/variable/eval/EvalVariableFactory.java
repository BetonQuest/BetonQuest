package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;

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
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalVariable(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalVariable(instruction);
    }

    private NullableVariableAdapter parseEvalVariable(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(".", instruction.getValueParts());
        return new NullableVariableAdapter(new EvalVariable(
                instruction, instruction.get(rawInstruction, Argument.STRING)
        ));
    }
}
