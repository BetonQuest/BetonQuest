package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.event.Event;

/**
 * A handler for events with profiles involved.
 *
 * @param <T> the event type
 */
@FunctionalInterface
public interface OnlineProfileEventHandler<T extends Event> {

    /**
     * This method gets called when the related event is triggered.
     *
     * @param event   the event that was triggered
     * @param profile the event-related profile extracted from the event
     * @throws QuestException when the event handling fails
     */
    void handle(T event, OnlineProfile profile) throws QuestException;
}
