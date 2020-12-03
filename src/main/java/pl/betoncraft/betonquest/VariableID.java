package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings("PMD.CommentRequired")
public class VariableID extends pl.betoncraft.betonquest.id.VariableID {

    /**
     * @deprecated Use the {@link pl.betoncraft.betonquest.id.VariableID},
     * this will be removed in 2.0 release
     */
    // TODO Delete in BQ 2.0.0
    @Deprecated
    public VariableID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
    }

}
