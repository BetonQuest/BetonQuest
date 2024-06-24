package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.betonquest.betonquest.api.quest.variable.VariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * A factory for creating Eval variables.
 */
public class EvalVariableFactory implements VariableFactory {
    /**
     * Create a new Eval variable factory.
     */
    public EvalVariableFactory() {
    }

    @Override
    public Variable parse(final Instruction instruction) throws InstructionParseException {
        final QuestPackage pack = instruction.getPackage();
        final String rawInstruction = String.join(".", instruction.getAllParts());
        return new EvalVariable(pack, new VariableString(pack, rawInstruction));
    }
}
