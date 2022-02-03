package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings({"PMD.ShortClassName", "PMD.CommentRequired"})
public class NoID extends ID {

    public NoID(final QuestPackage pack) throws ObjectNotFoundException {
        super(pack, "no-id");
    }

}
