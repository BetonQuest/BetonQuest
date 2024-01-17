package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.modules.config.patcher.migration.Migration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the remove entity migration.
 */
public class RemoveEntity implements Migration {

    /**
     * The configs to migrate.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new mmo_updates migrator.
     *
     * @param provider The config provider
     */
    public RemoveEntity(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final ConfigurationSection events = config.getConfigurationSection("events");
            if (events == null) {
                continue;
            }
            for (final String key : events.getKeys(false)) {
                final String value = events.getString(key);
                if (value == null) {
                    continue;
                }
                if (value.startsWith("clear ")) {
                    events.set(key, "removeentity " + value.substring("clear ".length()));
                    config.save(file);
                } else if (value.startsWith("killmob ")) {
                    events.set(key, "removeentity " + value.substring("killmob ".length()) + " kill");
                    config.save(file);
                }
            }
        }
    }
}
