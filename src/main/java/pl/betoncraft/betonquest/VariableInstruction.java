package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.id.ID;

public class VariableInstruction extends Instruction {

    public VariableInstruction(final ConfigPackage pack, final ID variableIdentifier, final String instruction) {
        super(pack, variableIdentifier, instruction);
        if (!instruction.startsWith("%") && !instruction.endsWith("%")) {
            throw new IllegalArgumentException("Variable instruction does not start and end with '%' character");
        }
        super.instruction = instruction.substring(1, instruction.length() - 1);
        super.parts = super.instruction.split("\\.");
    }

}
