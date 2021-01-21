package org.betonquest.betonquest.id;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableInstruction;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings("PMD.CommentRequired")
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
