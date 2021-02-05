package org.betonquest.betonquest.id;

import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidFieldNameMatchingMethodName"})
public class ConditionID extends ID {

    private final boolean inverted;

    public ConditionID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, removeExclamationMark(identifier));
        this.inverted = !identifier.isEmpty() && identifier.charAt(0) == '!';
        rawInstruction = super.pack.getString("conditions." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Condition '" + getFullID() + "' is not defined");
        }
    }

    private static String removeExclamationMark(final String identifier) {
        if (!identifier.isEmpty() && identifier.charAt(0) == '!') {
            return identifier.substring(1);
        }
        return identifier;
    }

    public boolean inverted() {
        return inverted;
    }

}
