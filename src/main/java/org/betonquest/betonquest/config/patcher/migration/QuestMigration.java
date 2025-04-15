package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Migrates a {@link Quest}.
 */
@FunctionalInterface
public interface QuestMigration {
    /**
     * Migrates the configs.
     *
     * @param quest the Quest to migrate
     * @throws InvalidConfigurationException if an error occurs
     */
    void migrate(Quest quest) throws InvalidConfigurationException;

    /**
     * Gets the section from the config and replaces the value that starts with the old value with the new value.
     *
     * @param config        The config
     * @param sectionName   The section name
     * @param oldStartValue The old value
     * @param newStartValue The new value
     */
    default void replaceStartValueInSection(final ConfigurationSection config, final String sectionName, final String oldStartValue, final String newStartValue) {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return;
        }
        final String oldStartValueSpace = oldStartValue + " ";
        final String newStartValueSpace = newStartValue + " ";
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith(oldStartValueSpace)) {
                section.set(key, newStartValueSpace + value.substring(oldStartValueSpace.length()));
            }
        }
    }

    /**
     * Gets the section from the config and replaces the old value with the new value.
     *
     * @param config      The config
     * @param sectionName The section name
     * @param typeName    The type name
     * @param oldValue    The old value
     * @param newValue    The new value
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    default void replaceValueInSection(final ConfigurationSection config, final String sectionName, final String typeName, final String oldValue, final String newValue) {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith(typeName + " ") && value.contains(oldValue)) {
                section.set(key, value.replace(oldValue, newValue));
            }
        }
    }
}
