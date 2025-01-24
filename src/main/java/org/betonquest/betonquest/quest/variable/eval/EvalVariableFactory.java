package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

/**
 * A factory for creating Eval variables.
 */
public class EvalVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {
    /**
     * Variable processor that the eval variable should use for creating variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new Eval variable factory.
     *
     * @param variableProcessor variable processor to use
     */
    public EvalVariableFactory(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
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
        final QuestPackage pack = instruction.getPackage();
        final String rawInstruction = String.join(".", instruction.getValueParts());
        return new NullableVariableAdapter(new EvalVariable(
                variableProcessor, pack,
                new VariableString(variableProcessor, pack, rawInstruction)
        ));
    }
}
