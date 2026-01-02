package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

/**
 * A handler for static non-profile events.
 *
 * @param <T> the event type
 */
public interface StaticEventHandler<T extends Event> {

    /**
     * This method gets called when the related event is triggered.
     *
     * @param event    the event that was triggered
     * @param priority the priority the event was triggered with
     * @throws QuestException when the event handling fails
     */
    void handle(T event, EventPriority priority) throws QuestException;
}
