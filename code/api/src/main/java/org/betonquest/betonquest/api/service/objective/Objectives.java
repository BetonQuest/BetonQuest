package org.betonquest.betonquest.api.service.objective;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Provides access to the objective service.
 * <br><br>
 * Use the {@link ObjectiveRegistry} to register new objective types and the {@link ObjectiveManager} to manage them.
 *
 * @since 3.0.0
 */
public interface Objectives extends ServiceFeature<ObjectiveManager, ObjectiveRegistry> {

}
