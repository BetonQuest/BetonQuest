package org.betonquest.betonquest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ObjectiveID;

@SuppressWarnings("PMD.CommentRequired")
public class VariableInstruction extends Instruction {

    public VariableInstruction(final QuestPackage pack, final ID variableIdentifier, final String instruction) {
        super(BetonQuest.getInstance().getLoggerFactory().create(VariableInstruction.class), pack, variableIdentifier, instruction);
        if (!instruction.isEmpty() && instruction.charAt(0) != '%' && !instruction.endsWith("%")) {
            throw new IllegalArgumentException("Variable instruction does not start and end with '%' character");
        }
        super.instruction = instruction.substring(1, instruction.length() - 1);
        super.parts = super.instruction.split("\\.");
    }

    @Override
    public VariableInstruction copy() {
        return new VariableInstruction(getPackage(), getID(), "%" + instruction + "%");
    }

    @Override
    public Instruction copy(final ObjectiveID newID) {
        return new VariableInstruction(getPackage(), newID, "%" + instruction + "%");
    }
}
