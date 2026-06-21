package org.betonquest.betonquest.api.service.item;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new items with the {@link ItemRegistry} or access existing ones with the {@link ItemManager}.
 *
 * @since 3.0.0
 */
public interface Items extends ServiceFeature<ItemManager, ItemRegistry> {

}
