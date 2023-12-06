package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * The First event. Similar to the folder, except it runs linearly through a list of events and
 * stops after the first one succeeds. This is intended to be used with condition: syntax in events.
 */
public class FirstEvent implements Event {
    /**
     * The events to run.
     */
    private final EventID[] events;

    /**
     * Makes a new first event.
     *
     * @param eventIDList A list of events to execute in order.
     */
    public FirstEvent(final List<EventID> eventIDList) {
        events = eventIDList.toArray(new EventID[0]);
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Deque<EventID> chosenList = new LinkedList<>(Arrays.asList(events));
        if (!chosenList.isEmpty()) {
            for (final EventID event : chosenList) {
                if (BetonQuest.event(profile, event)) {
                    break;
                }
            }
        }
    }
}
