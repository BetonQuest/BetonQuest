package org.betonquest.betonquest.api.quest.event;

/**
 * Interface for "static" quest-events.
 * It represents the "static" event as described in the BetonQuest user documentation.
 * They may act on all players, only online player or even no player at all; this is implementation detail.
 * For the normal event variant see {@link Event}.
 */
public interface StaticEvent {
    /**
     * Executes the "static" event.
     */
    void execute();
}
