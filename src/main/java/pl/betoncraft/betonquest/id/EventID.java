package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

public class EventID extends ID {

    public EventID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getString("events." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Event '" + getFullID() + "' is not defined");
        }
    }

}
