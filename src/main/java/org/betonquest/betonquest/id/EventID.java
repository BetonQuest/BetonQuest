package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class EventID extends ID {

    public EventID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "events", "Event");
    }
}
