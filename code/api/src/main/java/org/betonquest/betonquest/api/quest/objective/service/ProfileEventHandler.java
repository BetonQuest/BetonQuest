package org.betonquest.betonquest.api.quest.objective.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.event.Event;

/**
 * A handler for events with profiles involved.
 *
 * @param <T> the event type
 * @since 3.0.0
 */
@FunctionalInterface
public interface ProfileEventHandler<T extends Event> {

    /**
     * This method gets called when the related event is triggered.
     *
     * @param event   the event that was triggered
     * @param profile the event-related profile extracted from the event
     * @throws QuestException when the event handling fails
     * @since 3.0.0
     */
    void handle(T event, Profile profile) throws QuestException;
}
