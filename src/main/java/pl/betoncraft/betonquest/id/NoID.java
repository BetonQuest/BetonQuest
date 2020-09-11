package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings("PMD.ShortClassName")
public class NoID extends ID {

    public NoID(final ConfigPackage pack) throws ObjectNotFoundException {
        super(pack, "no-id");
    }

}
