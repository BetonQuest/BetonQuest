package org.betonquest.betonquest.api.config.quest;

import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * Represents the cache of a {@link QuestPackage} stored as a YAML file.
 *
 * @since 3.4.0
 */
public interface QuestPackageCache {

    /**
     * Provides the {@link ConfigurationSection} associated with the given key to the consumer,
     * creating it if necessary, so the consumer can write values to it.
     *
     * @param key          the key identifying the configuration section
     * @param cacheSection the consumer that writes values to the configuration section
     */
    void write(String key, QuestConsumer<ConfigurationSection> cacheSection);

    /**
     * Returns the {@link ConfigurationSection} for the given key to read values from.
     *
     * @param key the key identifying the configuration section
     * @return the {@link ConfigurationSection} for the given key, or an empty Optional if no cached value is present
     */
    Optional<ConfigurationSection> read(String key);
}
