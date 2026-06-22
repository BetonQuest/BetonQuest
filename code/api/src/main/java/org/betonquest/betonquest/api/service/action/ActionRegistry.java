package org.betonquest.betonquest.api.service.action;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * Stores the action factories.
 *
 * @since 3.0.0
 */
public interface ActionRegistry extends CoreQuestRegistry<PlayerAction, PlayerlessAction> {

}
