package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * ID identifying a {@link Schedule}.
 */
public class ScheduleID extends SectionIdentifier {

    /**
     * Construct a new ScheduleID in the given package from the provided identifier.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        package where the id is defined
     * @param identifier  string that defines the id
     * @throws QuestException if no schedule with this id exists
     */
    public ScheduleID(final QuestPackageManager packManager, final QuestPackage pack, final String identifier) throws QuestException {
        super(packManager, pack, identifier, "schedules", "Schedule");
    }
}
