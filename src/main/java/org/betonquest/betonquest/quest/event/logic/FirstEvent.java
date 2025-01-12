package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The First event. Similar to the folder, except it runs linearly through a list of events and
 * stops after the first one succeeds. This is intended to be used with condition: syntax in events.
 */
public class FirstEvent implements NullableEvent {
    /**
     * The events to run.
     */
    private final List<EventID> events;

    /**
     * Makes a new first event.
     *
     * @param eventIDList A list of events to execute in order.
     */
    public FirstEvent(final List<EventID> eventIDList) {
        events = eventIDList;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final EventID event : events) {
            if (BetonQuest.event(profile, event)) {
                break;
            }
        }
    }
}
