package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
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
     * The Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The list of events to execute.
     */
    private final Argument<List<EventID>> events;

    /**
     * Makes a new first event.
     *
     * @param questTypeApi the Quest Type API
     * @param events       the list of events to execute
     */
    public FirstEvent(final QuestTypeApi questTypeApi, final Argument<List<EventID>> events) {
        this.questTypeApi = questTypeApi;
        this.events = events;
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
