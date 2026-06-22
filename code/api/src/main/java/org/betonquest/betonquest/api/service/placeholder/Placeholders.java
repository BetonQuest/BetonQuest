package org.betonquest.betonquest.api.service.placeholder;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new placeholders with {@link PlaceholderRegistry} or access existing ones with {@link PlaceholderManager}.
 *
 * @since 3.0.0
 */
public interface Placeholders extends ServiceFeature<PlaceholderManager, PlaceholderRegistry> {

}
