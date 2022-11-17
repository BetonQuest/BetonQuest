package org.betonquest.betonquest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.id.ID;

@SuppressWarnings("PMD.CommentRequired")
public class VariableInstruction extends Instruction {

    public VariableInstruction(final QuestPackage pack, final ID variableIdentifier, final String instruction) {
        super(pack, variableIdentifier, instruction);
        if (!instruction.isEmpty() && instruction.charAt(0) != '%' && !instruction.endsWith("%")) {
            throw new IllegalArgumentException("Variable instruction does not start and end with '%' character");
        }
        super.instruction = instruction.substring(1, instruction.length() - 1);
        super.parts = super.instruction.split("\\.");
    }

    @Override
    public VariableInstruction copy() {
        return new VariableInstruction(super.getPackage(), super.getID(), "%" + instruction + "%");
    }
}
