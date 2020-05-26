package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

public class VariableID extends pl.betoncraft.betonquest.id.VariableID {

    /**
     * @deprecated Use the {@link pl.betoncraft.betonquest.id.VariableID} this
     *             will be removed in 13 release or later
     */
    public VariableID(ConfigPackage pack, String id) throws ObjectNotFoundException {
        super(pack, id);
    }

}
