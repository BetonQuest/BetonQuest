package org.betonquest.betonquest.api.service.objective;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new objectives with {@link ObjectiveRegistry} or access existing ones with {@link ObjectiveManager}.
 *
 * @since 3.0.0
 */
public interface Objectives extends ServiceFeature<ObjectiveManager, ObjectiveRegistry> {

}
