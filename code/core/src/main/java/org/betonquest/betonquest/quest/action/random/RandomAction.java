package org.betonquest.betonquest.quest.action.random;

import org.betonquest.betonquest.api.identifier.ActionIdentifier;

/**
 * Represents an action with its weight.
 *
 * @param actionID the action to be executed
 * @param weight   the weight of the action
 */
public record RandomAction(ActionIdentifier actionID, double weight) {

}
