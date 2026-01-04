package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.quest.action.ActionID;

/**
 * Represents an action with its weight.
 *
 * @param actionID the action to be executed
 * @param weight   the weight of the action
 */
public record RandomAction(ActionID actionID, double weight) {

}
