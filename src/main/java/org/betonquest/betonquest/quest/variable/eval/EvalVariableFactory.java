package org.betonquest.betonquest.quest.variable.eval;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.variable.Variable;
import org.betonquest.betonquest.api.quest.variable.VariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * A factory for creating Eval variables.
 */
public class EvalVariableFactory implements VariableFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create a new Eval variable factory.
     *
     * @param log the logger
     */
    public EvalVariableFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public Variable parse(final Instruction instruction) throws InstructionParseException {
        final QuestPackage pack = instruction.getPackage();
        final String rawInstruction = String.join(".", instruction.getAllParts());
        return new EvalVariable(log, pack, new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, rawInstruction));
    }
}
