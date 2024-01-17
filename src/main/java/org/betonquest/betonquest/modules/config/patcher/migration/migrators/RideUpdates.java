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
public class RideUpdates implements Migration {

    /**
     * The configs to migrate.
     */
    private final FileConfigurationProvider producer;

    /**
     * Creates a new mmo_updates migrator.
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
            final boolean objReplaced = replaceValue(config, "objectives", "vehicle");
            final boolean condReplaced = replaceValue(config, "conditions", "riding");
            if (objReplaced || condReplaced) {
                config.save(file);
            }
        }
    }

    private static boolean replaceValue(final YamlConfiguration config, final String sectionName, final String valueStart) throws IOException {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return false;
        }
        final String valueStartSpace = valueStart + " ";
        boolean replaced = false;
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith(valueStartSpace)) {
                section.set(key, "ride " + value.substring(valueStartSpace.length()));
                replaced = true;
            }
        }
        return replaced;
    }
}
