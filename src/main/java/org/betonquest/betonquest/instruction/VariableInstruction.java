package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.tokenizer.Tokenizer;

/**
 * The variable instruction. Primary object for variable input parsing.
 */
public class VariableInstruction extends Instruction {
    /**
     * Regular expression that can be used to split variables correctly.
     */
    private static final Tokenizer DOT_TOKENIZER = (instruction) -> instruction.split("\\.");

    /**
     * Constructs a new VariableInstruction with the given quest package, variable identifier, and instruction.
     *
     * @param pack        The quest package that this instruction belongs to.
     * @param identifier  The identifier of the variable.
     * @param instruction The instruction string. It should start and end with '%' character.
     * @throws QuestException if the instruction could not be tokenized,
     *                        or if the instruction does not start and end with '%' character.
     */
    public VariableInstruction(final QuestPackage pack, final ID identifier, final String instruction) throws QuestException {
        super(DOT_TOKENIZER, pack, identifier, cleanInstruction(instruction));
    }

    /**
     * Constructs a new VariableInstruction with the given quest package, variable identifier, and instruction.
     *
     * @param instruction The raw instruction string for this variable.
     * @param identifier  The identifier for this variable.
     */
    public VariableInstruction(final VariableInstruction instruction, final ID identifier) {
        super(instruction, identifier);
    }

    private static String cleanInstruction(final String instruction) throws QuestException {
        if (!instruction.isEmpty() && instruction.charAt(0) != '%' && !instruction.endsWith("%")) {
            throw new QuestException("Variable instruction does not start and end with '%' character");
        }
        return instruction.substring(1, instruction.length() - 1);
    }

    @Override
    public VariableInstruction copy() {
        return copy(getID());
    }

    @Override
    public VariableInstruction copy(final ID newID) {
        return new VariableInstruction(this, newID);
    }
}
