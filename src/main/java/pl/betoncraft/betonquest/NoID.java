package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.NoID} instead, this
 *             this will be removed in 13 release or later
 */
public class NoID extends pl.betoncraft.betonquest.id.NoID {

    public NoID(ConfigPackage pack) throws ObjectNotFoundException {
        super(pack);
    }

}
