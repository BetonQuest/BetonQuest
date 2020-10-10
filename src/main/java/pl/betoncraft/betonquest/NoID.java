package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.NoID} instead,
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
@SuppressWarnings("PMD.ShortClassName")
public class NoID extends pl.betoncraft.betonquest.id.NoID {

    public NoID(final ConfigPackage pack) throws ObjectNotFoundException {
        super(pack);
    }

}
