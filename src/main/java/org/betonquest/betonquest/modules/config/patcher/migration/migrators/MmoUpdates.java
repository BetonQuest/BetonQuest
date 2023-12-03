package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileProducer;
import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        return configs.values().stream()
                .map(config -> config.getConfigurationSection("objectives"))
                .filter(Objects::nonNull)
                .anyMatch(config -> config.getKeys(false).stream()
                        .map(config::getString)
                        .filter(Objects::nonNull)
                        .anyMatch(value -> value.startsWith("mmocorecastskill") || value.startsWith("mmoitemcastability")));
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final ConfigurationSection objectives = config.getConfigurationSection("objectives");
            if (objectives == null) {
                continue;
            }
            for (final String key : objectives.getKeys(false)) {
                final String value = config.getString("objectives." + key);
                if (value == null) {
                    continue;
                }
                if (value.startsWith("mmocorecastskill")) {
                    config.set("objectives." + key, "mmoskill " + value.substring("mmocorecastskill ".length()) + " trigger:CAST");
                    config.save(file);
                } else if (value.startsWith("mmoitemcastability")) {
                    config.set("objectives." + key, "mmoskill " + value.substring("mmoitemcastability ".length()) + " trigger:RIGHT_CLICK");
                    config.save(file);
                }
            }
        }
    }
}
