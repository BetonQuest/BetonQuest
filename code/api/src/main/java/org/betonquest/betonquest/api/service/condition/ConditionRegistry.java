package org.betonquest.betonquest.api.service.condition;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;

/**
 * A registry for condition factories.
 *
 * @since 3.0.0
 */
public interface ConditionRegistry extends CoreQuestRegistry<PlayerCondition, PlayerlessCondition> {

}
