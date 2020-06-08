package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.ID} instead, this will
 *             this will be removed in 13 release or later
 */
public abstract class ID extends pl.betoncraft.betonquest.id.ID {

    public ID(ConfigPackage pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

}
