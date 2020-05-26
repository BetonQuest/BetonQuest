package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.ObjectiveID} instead,
 *             this will be removed in 13 release or later
 */
public class ObjectiveID extends pl.betoncraft.betonquest.id.ObjectiveID {

    public ObjectiveID(ConfigPackage pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

}
