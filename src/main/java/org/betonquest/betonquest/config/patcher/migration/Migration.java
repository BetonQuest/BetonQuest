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
     * @param oldStartValue The old value
     * @param newStartValue The new value
     * @return true if the value was replaced, false otherwise
     */
    default boolean replaceStartValueInSection(final YamlConfiguration config, final String sectionName, final String oldStartValue, final String newStartValue) {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return false;
        }
        final String oldStartValueSpace = oldStartValue + " ";
        final String newStartValueSpace = newStartValue + " ";
        boolean replaced = false;
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith(oldStartValueSpace)) {
                section.set(key, newStartValueSpace + value.substring(oldStartValueSpace.length()));
                replaced = true;
            }
        }
        return replaced;
    }

    /**
     * Gets the section from the config and replaces the old value with the new value.
     *
     * @param config      The config
     * @param sectionName The section name
     * @param typeName    The type name
     * @param oldValue    The old value
     * @param newValue    The new value
     * @return true if the value was replaced, false otherwise
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    default boolean replaceValueInSection(final YamlConfiguration config, final String sectionName, final String typeName, final String oldValue, final String newValue) {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return false;
        }
        boolean replaced = false;
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith(typeName + " ") && value.contains(oldValue)) {
                section.set(key, value.replace(oldValue, newValue));
                replaced = true;
            }
        }
        return replaced;
    }
}
