package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.EventID} instead, this
 *             this will be removed in 13 release or later
 */
public class EventID extends pl.betoncraft.betonquest.id.EventID {

    public EventID(ConfigPackage pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

}
