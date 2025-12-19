package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Event.
 */
public class EventID extends InstructionIdentifier {

    /**
     * Create a new Event ID.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the package of the event
     * @param identifier  the complete identifier of the event
     * @throws QuestException if there is no such event
     */
    public EventID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(packManager, pack, identifier, "events", "Event");
    }
}
