package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;

@SuppressWarnings({"PMD.ShortClassName", "PMD.CommentRequired"})
public class NoID extends ID {

    public NoID(final QuestPackage pack) throws QuestException {
        super(pack, "no-id");
    }
}
