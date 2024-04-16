package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class EventID extends ID {

    public EventID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getString("events." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Event '" + getFullID() + "' is not defined");
        }
    }

}
