package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * The variable instruction. Primary object for variable input parsing.
 */
public class VariableInstruction extends Instruction {
    /**
     * Regular expression that can be used to split variables correctly.
     */
    private static final Tokenizer DOT_TOKENIZER = (instruction) -> instruction.split("\\.");

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Constructs a new VariableInstruction with the given quest package, variable identifier, and instruction.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        The quest package that this instruction belongs to.
     * @param identifier  The identifier of the variable.
     * @param instruction The instruction string. It should start and end with '%' character.
     * @throws QuestException if the instruction could not be tokenized,
     *                        or if the instruction does not start and end with '%' character.
     */
    public VariableInstruction(final QuestPackageManager packManager, final QuestPackage pack,
                               final Identifier identifier, final String instruction) throws QuestException {
        super(packManager, DOT_TOKENIZER, pack, identifier, cleanInstruction(instruction));
        this.packManager = packManager;
    }

    /**
     * Constructs a new VariableInstruction with the given quest package, variable identifier, and instruction.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param instruction The raw instruction string for this variable.
     * @param identifier  The identifier for this variable.
     */
    public VariableInstruction(final QuestPackageManager packManager, final VariableInstruction instruction, final Identifier identifier) {
        super(packManager, instruction, identifier);
        this.packManager = packManager;
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
    public VariableInstruction copy(final Identifier newID) {
        return new VariableInstruction(packManager, this, newID);
    }
}
