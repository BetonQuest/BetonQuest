package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the EventScheduling migration.
 */
public class EventScheduling implements Migration {

    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new EventScheduling migrator.
     *
     * @param provider The config provider
     */
    public EventScheduling(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
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
