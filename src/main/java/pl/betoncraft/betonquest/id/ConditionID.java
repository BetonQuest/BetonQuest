package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

public class ConditionID extends ID {

    private final boolean inverted;

    public ConditionID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, removeExclamationMark(identifier));
        this.inverted = identifier.startsWith("!");
        rawInstruction = super.pack.getString("conditions." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Condition '" + getFullID() + "' is not defined");
        }
    }

    private static String removeExclamationMark(final String identifier) {
        if (identifier.startsWith("!")) {
            return identifier.substring(1);
        }
        return identifier;
    }

    public boolean inverted() {
        return inverted;
    }

}
