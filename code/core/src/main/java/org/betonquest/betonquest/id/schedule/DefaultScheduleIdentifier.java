package org.betonquest.betonquest.id.schedule;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.ScheduleIdentifier;

/**
 * The default implementation for {@link ScheduleIdentifier}s.
 */
public class DefaultScheduleIdentifier extends DefaultIdentifier implements ScheduleIdentifier {

    /**
     * Creates a new schedule identifier.
     *
     * @param pack       the package the identifier is related to
     * @param identifier the identifier of the schedule
     */
    protected DefaultScheduleIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
