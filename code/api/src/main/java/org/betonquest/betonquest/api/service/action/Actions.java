package org.betonquest.betonquest.api.service.action;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Provides access to the action service.
 * <p>
 * Use the {@link ActionRegistry} to register new actions and the {@link ActionManager} to execute them.
 *
 * @since 3.0.0
 */
public interface Actions extends ServiceFeature<ActionManager, ActionRegistry> {

}
