package org.betonquest.betonquest.api.service.condition;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;

/**
 * Stores the condition factories.
 *
 * @since 3.0.0
 */
public interface ConditionRegistry extends CoreQuestRegistry<PlayerCondition, PlayerlessCondition> {

}
