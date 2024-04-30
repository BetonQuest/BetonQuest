package org.betonquest.betonquest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.ID;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the variable-related instructions in BetonQuest.
 */
public class VariableInstruction extends Instruction {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Constructs a new VariableInstruction with the given logger, quest package, variable identifier, and instruction.
     *
     * @param log                The logger used for logging.
     * @param pack               The quest package that this instruction belongs to.
     * @param variableIdentifier The identifier of the variable.
     * @param instruction        The instruction string. It should start and end with '%' character.
     * @throws IllegalArgumentException if the instruction string does not start and end with '%' character.
     */
    public VariableInstruction(final BetonQuestLogger log, final QuestPackage pack, @Nullable final ID variableIdentifier, final String instruction) {
        super(log, pack, variableIdentifier, instruction);
        this.log = log;
        if (!instruction.isEmpty() && instruction.charAt(0) != '%' && !instruction.endsWith("%")) {
            throw new IllegalArgumentException("Variable instruction does not start and end with '%' character");
        }
        super.parts = instruction.substring(1, instruction.length() - 1).split("\\.");
    }

    @Override
    public String getInstruction() {
        return String.join(".", super.parts);
    }

    @Override
    public VariableInstruction copy() {
        return copy(getID());
    }

    @Override
    public VariableInstruction copy(@Nullable final ID newID) {
        return new VariableInstruction(log, getPackage(), newID, "%" + getInstruction() + "%");
    }
}
