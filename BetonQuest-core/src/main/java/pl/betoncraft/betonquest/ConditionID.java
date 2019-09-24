package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.pack.ConfigContainer;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.ConditionID} instead,
 *             this will be removed in 13 release or later
 */
public class ConditionID extends pl.betoncraft.betonquest.id.ConditionID {

    public ConditionID(ConfigContainer pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

}
