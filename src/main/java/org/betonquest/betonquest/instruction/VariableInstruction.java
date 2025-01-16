package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.ID;

import java.util.regex.Pattern;

/**
 * This class represents the variable-related instructions in BetonQuest.
 */
public class VariableInstruction extends Instruction {
    /**
     * Regular expression that can be used to split variables correctly.
     */
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

    /**
     * Constructs a new VariableInstruction with the given logger, quest package, variable identifier, and instruction.
     *
     * @param log                The logger used for logging.
     * @param pack               The quest package that this instruction belongs to.
     * @param variableIdentifier The identifier of the variable.
     * @param instruction        The instruction string. It should start and end with '%' character.
     * @throws IllegalArgumentException if the instruction string does not start and end with '%' character.
     */
    public VariableInstruction(final BetonQuestLogger log, final QuestPackage pack, final ID variableIdentifier, final String instruction) {
        super(DOT_PATTERN::split, log, pack, variableIdentifier, cleanInstruction(instruction));
    }

    /**
     * Constructs a new VariableInstruction with the given logger, quest package, variable identifier, and instruction.
     *
     * @param pack               The quest package that this instruction belongs to.
     * @param variableIdentifier The identifier of the variable.
     * @param instruction        The raw instruction string for this variable.
     * @param parts              The variable instruction parts.
     */
    public VariableInstruction(final QuestPackage pack, final ID variableIdentifier, final String instruction, final String... parts) {
        super(pack, variableIdentifier, instruction, parts);
    }

    private static String cleanInstruction(final String instruction) {
        if (!instruction.isEmpty() && instruction.charAt(0) != '%' && !instruction.endsWith("%")) {
            throw new IllegalArgumentException("Variable instruction does not start and end with '%' character");
        }
        return instruction.substring(1, instruction.length() - 1);
    }

    @Override
    public VariableInstruction copy() {
        return copy(getID());
    }

    @Override
    public VariableInstruction copy(final ID newID) {
        return new VariableInstruction(getPackage(), newID, instructionString, getParts());
    }
}
