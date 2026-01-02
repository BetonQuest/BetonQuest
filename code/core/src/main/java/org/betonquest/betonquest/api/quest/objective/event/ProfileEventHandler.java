package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

/**
 * A handler for events with profiles involved.
 *
 * @param <T> the event type
 */
public interface ProfileEventHandler<T extends Event> {

    /**
     * This method gets called when the related event is triggered.
     *
     * @param event    the event that was triggered
     * @param priority the priority the event was triggered with
     * @param profile  the event-related profile extracted from the event
     * @throws QuestException when the event handling fails
     */
    void handle(T event, EventPriority priority, Profile profile) throws QuestException;
}
