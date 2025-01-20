package org.betonquest.betonquest.config.patcher.migration.migrators.from1to2;

import org.betonquest.betonquest.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the Ride migration.
 */
public class RideUpdates implements Migration {

    /**
     * The configs to migrate.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new ride migrator.
     *
     * @param provider The config provider
     */
    public RideUpdates(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final boolean objReplaced = replaceStartValueInSection(config, "objectives", "vehicle", "ride");
            final boolean condReplaced = replaceStartValueInSection(config, "conditions", "riding", "ride");
            if (objReplaced || condReplaced) {
                config.save(file);
            }
        }
    }
}
