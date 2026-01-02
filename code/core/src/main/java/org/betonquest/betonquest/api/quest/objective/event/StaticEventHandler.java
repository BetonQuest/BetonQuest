package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;

/**
 * A handler for static non-profile events.
 *
 * @param <T> the event type
 */
@FunctionalInterface
public interface StaticEventHandler<T extends Event> {

    /**
     * This method gets called when the related event is triggered.
     *
     * @param event the event that was triggered
     * @throws QuestException when the event handling fails
     */
    void handle(T event) throws QuestException;
}
