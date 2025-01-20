package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the MmoUpdates migration.
 */
public class MmoUpdates implements Migration {

    /**
     * The configs to migrate.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new mmo_updates migrator.
     *
     * @param provider The config provider
     */
    public MmoUpdates(final FileConfigurationProvider provider) {
        this.producer = provider;
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
                final String value = objectives.getString(key);
                if (value == null) {
                    continue;
                }
                if (value.startsWith("mmocorecastskill ")) {
                    objectives.set(key, "mmoskill " + value.substring("mmocorecastskill ".length()) + " trigger:CAST");
                    config.save(file);
                } else if (value.startsWith("mmoitemcastability ")) {
                    objectives.set(key, "mmoskill " + value.substring("mmoitemcastability ".length()) + " trigger:RIGHT_CLICK");
                    config.save(file);
                }
            }
        }
    }
}
