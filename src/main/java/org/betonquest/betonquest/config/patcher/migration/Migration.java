package org.betonquest.betonquest.config.patcher.migration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

/**
 * Handles the migration process.
 */
public interface Migration {
    /**
     * Migrates the configs.
     *
     * @throws IOException if an error occurs
     */
    void migrate() throws IOException;

    /**
     * Gets the section from the config and replaces the value that starts with the old value with the new value.
     *
     * @param config        The config
     * @param sectionName   The section name
     * @param valueStartOld The old value
     * @param valueStartNew The new value
     * @return true if the value was replaced, false otherwise
     */
    default boolean replaceStartValueInSection(final YamlConfiguration config, final String sectionName, final String valueStartOld, final String valueStartNew) {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return false;
        }
        final String valueStartOldSpace = valueStartOld + " ";
        final String valueStartNewSpace = valueStartNew + " ";
        boolean replaced = false;
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith(valueStartOldSpace)) {
                section.set(key, valueStartNewSpace + value.substring(valueStartOldSpace.length()));
                replaced = true;
            }
        }
        return replaced;
    }
}
