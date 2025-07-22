package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.Variable;
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
    private final Variable<List<EventID>> events;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Makes a new first event.
     *
     * @param eventIDList  A list of events to execute in order.
     * @param questTypeAPI the Quest Type API
     */
    public FirstEvent(final Variable<List<EventID>> eventIDList, final QuestTypeAPI questTypeAPI) {
        events = eventIDList;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final EventID event : events.getValue(profile)) {
            if (questTypeAPI.event(profile, event)) {
                break;
            }
        }
    }
}
