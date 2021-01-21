package org.betonquest.betonquest.id;

import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveID extends ID {

    public ObjectiveID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getString("objectives." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Objective '" + getFullID() + "' is not defined");
        }
    }

}
