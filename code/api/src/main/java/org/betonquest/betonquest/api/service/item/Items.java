package org.betonquest.betonquest.api.service.item;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Provides access to the item service.
 * <p>
 * Use the {@link ItemRegistry} to register new items and the {@link ItemManager} to retrieve them.
 *
 * @since 3.0.0
 */
public interface Items extends ServiceFeature<ItemManager, ItemRegistry> {

}
