package org.betonquest.betonquest.meta;

import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Provides access to metadata and its history.
 */
public interface MetaDataProvider {

    /**
     * Get the most recent value for a key.
     * The returned ConfigurationSection(s) is a copy of the original that is unmodifiable.
     *
     * @param key the key to get the value for
     * @return the most recent value for the key as in optional in case there is no value present
     */
    Optional<Map.Entry<Instant, ConfigurationSection>> getMostRecent(String key);

    /**
     * Get only the latest values for a key, with timestamps.
     * The returned ConfigurationSection(s) is a copy of the original that is unmodifiable.
     *
     * @param key  the key to get the values for
     * @param time the amount of time to offset the current time
     * @param unit the unit of time to offset the current time
     * @return the latest values for the key, with timestamps
     */
    Map<Instant, ConfigurationSection> getRecent(String key, long time, ChronoUnit unit);

    /**
     * Get the entire history of values for a key, with timestamps.
     * The returned ConfigurationSection(s) is a copy of the original that is unmodifiable.
     *
     * @param key the key to get the history for
     * @return the history of values for the key, with timestamps
     */
    Map<Instant, ConfigurationSection> getAll(String key);

    /**
     * Query the metadata and select the desired value.
     * The returned ConfigurationSection(s) is a copy of the original that is unmodifiable.
     *
     * @param key   the key to query
     * @param query the query to execute
     * @param <T>   the type of the desired value
     * @return the desired value
     */
    <T> T query(String key, Function<Map<Instant, ConfigurationSection>, T> query);
}
