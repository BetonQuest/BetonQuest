package org.betonquest.betonquest.id;

import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings({"PMD.ShortClassName", "PMD.CommentRequired"})
public class NoID extends ID {

    public NoID(final ConfigPackage pack) throws ObjectNotFoundException {
        super(pack, "no-id");
    }

}
