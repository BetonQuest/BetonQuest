package org.betonquest.betonquest.modules.logger.custom.chat;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This interface provides methods to manage filters for a player.
 */
public interface PlayerFilter {
    /**
     * Adds a filter to a player.
     *
     * @param uuid   The {@link UUID} of the player
     * @param filter The filter pattern
     * @param level  The {@link Level} of the filter
     * @return True if the filter was successfully added
     */
    boolean addFilter(UUID uuid, String filter, Level level);

    /**
     * Removes a filter from a player.
     *
     * @param uuid   The {@link UUID} of the player
     * @param filter The filter pattern
     * @return True if the filter was successfully removed
     */
    boolean removeFilter(UUID uuid, String filter);

    /**
     * Gets a players filters.
     *
     * @param uuid The {@link UUID} of the player
     * @return A list of filters
     */
    List<String> getFilters(UUID uuid);

    /**
     * Get all player {@link UUID}s, that have active filters.
     *
     * @return A set of {@link UUID}s
     */
    Set<UUID> getUUIDs();

    /**
     * Checks if the given key will pass the players filter or not.
     *
     * @param uuid  The {@link UUID} of the player
     * @param key   The key that should be checked by the filters.
     * @param level The level related to the key.
     * @return True if it fits the active filter.
     */
    boolean filter(UUID uuid, String key, Level level);
}
