package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileConfigurationProvider;
import org.betonquest.betonquest.modules.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handles the fabled rename migration.
 */
public class FabledRename implements Migration {
    /**
     * The config producer.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new fabled migrator.
     *
     * @param provider The config provider
     */
    public FabledRename(final FileConfigurationProvider provider) {
        this.producer = provider;
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final boolean cond1Replaced = replaceStartValueInSection(config, "conditions", "skillapiclass", "fabledclass");
            final boolean cond2Replaced = replaceStartValueInSection(config, "conditions", "skillapilevel", "fabledlevel");
            if (cond1Replaced || cond2Replaced) {
                config.save(file);
            }
        }
    }
}
