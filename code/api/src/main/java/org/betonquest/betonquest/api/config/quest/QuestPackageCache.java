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
     * Created or gives the {@link ConfigurationSection} for the given key, to write values there.
     *
     * @param key          the key
     * @param cacheSection the {@link QuestConsumer} to cache values there
     */
    void write(String key, QuestConsumer<ConfigurationSection> cacheSection);

    /**
     * Returns the {@link ConfigurationSection} for the given key to read values from.
     *
     * @param key the key
     * @return the {@link ConfigurationSection} for the given key, or an empty Optional if no cached value is present.
     */
    Optional<ConfigurationSection> read(String key);
}
