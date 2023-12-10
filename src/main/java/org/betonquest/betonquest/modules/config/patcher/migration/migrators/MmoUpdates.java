package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.modules.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the mmo_updates migration.
 */
public class MmoUpdates implements Migration {

    /**
     * The configs to migrate.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new mmo_updates migrator.
     *
     * @param producer The config producer
     */
    public MmoUpdates(final FileConfigurationProvider producer) {
        this.producer = producer;
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
