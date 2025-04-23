package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.id.EventID;

/**
 * Represents an event with its chance.
 *
 * @param eventID the event to be executed
 * @param chance  the chance of the event
 */
public record RandomEvent(EventID eventID, Double chance) {
}
