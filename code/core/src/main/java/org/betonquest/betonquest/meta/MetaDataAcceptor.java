package org.betonquest.betonquest.meta;

import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.bukkit.configuration.ConfigurationSection;
import org.intellij.lang.annotations.Pattern;

import java.time.Instant;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Accepts metadata to be stored.
 */
public interface MetaDataAcceptor {

    /**
     * The pattern for metadata keys.
     */
    String META_DATA_KEY_PATTERN = "[a-z0-9_]+";

    /**
     * Stores metadata with a timestamp for the given key.
     *
     * @param key             the key to store the metadata for; the key is used as the file name
     * @param sectionConsumer the consumer to provide the metadata to
     */
    void accept(@Pattern(META_DATA_KEY_PATTERN) String key, QuestConsumer<ConfigurationSection> sectionConsumer);

    /**
     * Stores metadata with a timestamp for the given key if the metadata is different from the most recent metadata.
     *
     * @param key             the key to store the metadata for; the key is used as the file name
     * @param sectionConsumer the consumer to provide the metadata to
     * @param changePredicate the predicate to determine if the metadata is different from the most recent metadata
     */
    void acceptChange(@Pattern(META_DATA_KEY_PATTERN) String key, QuestConsumer<ConfigurationSection> sectionConsumer,
                      Predicate<Map.Entry<Instant, ConfigurationSection>> changePredicate);
}
