package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handels the EventScheduling migration.
 */
public class EventScheduling implements Migrator {

    /**
     * The configs to migrate.
     */
    private final Map<File, YamlConfiguration> configs;

    /**
     * Creates a new EventScheduling migrator.
     *
     * @param configs The configs to migrate.
     */
    public EventScheduling(final Map<File, YamlConfiguration> configs) {
        this.configs = configs;
    }

    @Override
    public boolean needMigration() {
        return configs.values().stream().anyMatch(config -> config.contains("static"));
    }

    @Override
    public void migrate() throws IOException {
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final ConfigurationSection staticSection = config.getConfigurationSection("static");
            if (staticSection != null) {
                staticSection.getValues(false).forEach((key, value) -> {
                    config.set("schedules." + key + ".type", "realtime-daily");
                    config.set("schedules." + key + ".time", key);
                    config.set("schedules." + key + ".events", value);
                });
                config.set("static", null);
                config.save(file);
            }
        }
    }
}
