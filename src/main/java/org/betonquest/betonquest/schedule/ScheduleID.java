package org.betonquest.betonquest.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.schedule.Schedule;

/**
 * ID identifying a {@link Schedule}.
 */
public class ScheduleID extends SectionIdentifier {

    /**
     * Construct a new ScheduleID in the given package from the provided identifier.
     *
     * @param pack       package where the id is defined
     * @param identifier string that defines the id
     * @throws QuestException if no schedule with this id exists
     */
    public ScheduleID(final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "schedules", "Schedule");
    }
}
