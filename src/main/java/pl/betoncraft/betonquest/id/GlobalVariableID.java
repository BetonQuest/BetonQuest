package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableInstruction;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

public class GlobalVariableID extends ID {
    public GlobalVariableID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
    }

    @Override
    public Instruction generateInstruction() {
        return new VariableInstruction(pack, this, identifier);
    }

    @Override
    public String getFullID() {
        return pack.getName() + "-" + getBaseID();
    }

}
