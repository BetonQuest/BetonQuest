package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.quest.action.ActionID;

/**
 * Represents an event with its weight.
 *
 * @param actionID the event to be executed
 * @param weight   the weight of the event
 */
public record RandomEvent(ActionID actionID, double weight) {

}
