package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileProducer;
import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handels the mmo_updates migration.
 */
public class MmoUpdates implements Migrator {

    /**
     * The configs to migrate.
     */
    private final FileProducer producer;

    /**
     * Creates a new mmo_updates migrator.
     *
     * @param producer The config producer
     */
    public MmoUpdates(final FileProducer producer) {
        this.producer = producer;
    }

    @Override
    public boolean needMigration() throws IOException {
        return getObjectiveSectionsWithOldMmoInstruction().findAny().isPresent();
    }

    @Override
    public void migrate() throws IOException {
        getObjectiveSectionsWithOldMmoInstruction().forEach(section -> section.getKeys(false).forEach(key -> {
            final String value = section.getString(key);
            if (value != null) {
                if (value.startsWith("mmocorecastskill")) {
                    section.set(key, "mmoskill " + value.substring("mmocorecastskill".length()) + " trigger:CAST");
                } else if (value.startsWith("mmoitemcastability")) {
                    section.set(key, "mmoskill " + value.substring("mmoitemcastability".length()) + " trigger:RIGHT_CLICK");
                }
            }
        }));

    }

    private Stream<ConfigurationSection> getObjectiveSectionsWithOldMmoInstruction() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        return configs.values().stream()
                .map(config -> config.getConfigurationSection("objectives"))
                .filter(Objects::nonNull)
                .filter(config -> config.getKeys(false).stream()
                        .map(config::getString)
                        .filter(Objects::nonNull)
                        .anyMatch(value -> value.startsWith("mmocorecastskill") || value.startsWith("mmoitemcastability")));
    }
}
