package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.GlobalVariableID} this
 * will be removed in 13 release or later
 */
public class GlobalVariableID extends pl.betoncraft.betonquest.id.GlobalVariableID {

    public GlobalVariableID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
    }

}
