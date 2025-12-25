package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.quest.event.EventID;

/**
 * Represents an event with its weight.
 *
 * @param eventID the event to be executed
 * @param weight  the weight of the event
 */
public record RandomEvent(EventID eventID, double weight) {

}
