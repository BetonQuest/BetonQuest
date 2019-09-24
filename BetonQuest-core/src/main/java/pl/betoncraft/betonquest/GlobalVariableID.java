package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.pack.ConfigContainer;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.GlobalVariableID} this
 *             will be removed in 13 release or later
 */
public class GlobalVariableID extends pl.betoncraft.betonquest.id.GlobalVariableID {

    public GlobalVariableID(ConfigContainer pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

}
