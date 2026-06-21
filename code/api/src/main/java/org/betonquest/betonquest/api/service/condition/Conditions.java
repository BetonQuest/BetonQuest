package org.betonquest.betonquest.api.service.condition;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new conditions with the {@link ConditionRegistry} or test existing ones with the {@link ConditionManager}.
 *
 * @since 3.0.0
 */
public interface Conditions extends ServiceFeature<ConditionManager, ConditionRegistry> {

}
