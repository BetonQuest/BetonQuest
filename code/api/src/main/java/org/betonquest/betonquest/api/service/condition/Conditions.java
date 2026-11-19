package org.betonquest.betonquest.api.service.condition;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Provides access to the condition service.
 * <br><br>
 * Use the {@link ConditionRegistry} to register new conditions and the {@link ConditionManager} to test them.
 *
 * @since 3.0.0
 */
public interface Conditions extends ServiceFeature<ConditionManager, ConditionRegistry> {

}
