package pl.betoncraft.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.ObjectiveID} instead,
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
@SuppressWarnings("PMD.CommentRequired")
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class ObjectiveID extends pl.betoncraft.betonquest.id.ObjectiveID {

    public ObjectiveID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
    }

}
