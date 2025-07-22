package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Event.
 */
public class EventID extends ID {

    /**
     * Create a new Event ID.
     *
     * @param pack       the package of the event
     * @param identifier the complete identifier of the event
     * @throws QuestException if there is no such event
     */
    public EventID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "events", "Event");
    }
}
