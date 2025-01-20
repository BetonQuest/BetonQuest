package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
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
            final boolean event1Replaced = replaceStartValueInSection(config, "events", "clear", "removeentity");
            final boolean event2Replaced = replaceStartValueInSection(config, "events", "killmob", "removeentity");
            if (event1Replaced || event2Replaced) {
                config.save(file);
            }
        }
    }
}
