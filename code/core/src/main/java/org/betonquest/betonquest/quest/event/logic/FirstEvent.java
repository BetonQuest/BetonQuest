package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
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
    private final QuestTypeApi questTypeApi;

    /**
     * Makes a new first event.
     *
     * @param eventIDList  A list of events to execute in order.
     * @param questTypeApi the Quest Type API
     */
    public FirstEvent(final Variable<List<EventID>> eventIDList, final QuestTypeApi questTypeApi) {
        events = eventIDList;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        for (final EventID event : events.getValue(profile)) {
            if (questTypeApi.event(profile, event)) {
                break;
            }
        }
    }
}
