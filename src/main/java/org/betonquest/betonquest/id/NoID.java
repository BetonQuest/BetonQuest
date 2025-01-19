package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exception.ObjectNotFoundException;

@SuppressWarnings({"PMD.ShortClassName", "PMD.CommentRequired"})
public class NoID extends ID {

    public NoID(final QuestPackage pack) throws ObjectNotFoundException {
        super(pack, "no-id");
    }

}
