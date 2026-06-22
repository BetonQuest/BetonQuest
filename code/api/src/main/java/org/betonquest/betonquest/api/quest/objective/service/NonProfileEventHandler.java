package org.betonquest.betonquest.api.quest.objective.service;

import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;

/**
 * A handler for non-profile events.
 *
 * @param <T> the event type
 * @since 3.0.0
 */
@FunctionalInterface
public interface NonProfileEventHandler<T extends Event> {

    /**
     * This method gets called when the related event is triggered.
     *
     * @param event the event that was triggered
     * @throws QuestException when the event handling fails
     * @since 3.0.0
     */
    void handle(T event) throws QuestException;
}
