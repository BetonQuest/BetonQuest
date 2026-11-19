package org.betonquest.betonquest.api.service.placeholder;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Provides access to the placeholder service.
 * <br><br>
 * Use the {@link PlaceholderRegistry} to register new placeholders and the {@link PlaceholderManager} to resolve them.
 *
 * @since 3.0.0
 */
public interface Placeholders extends ServiceFeature<PlaceholderManager, PlaceholderRegistry> {

}
