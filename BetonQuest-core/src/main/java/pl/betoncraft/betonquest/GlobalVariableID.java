package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;

public class GlobalVariableID extends ID {
	public GlobalVariableID(ConfigPackage pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

    @Override
    public Instruction generateInstruction() {
        return new VariableInstruction(pack, this, id);
    }

    @Override
    public String getFullID() {
        return pack.getName() + "-" + getBaseID();
    }

}
