package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.function.Function;

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
     * @param config        the config
     * @param sectionName   the section name
     * @param oldStartValue the old value
     * @param newStartValue the new value
     */
    default void replaceStartValueInSection(final ConfigurationSection config, final String sectionName,
                                            final String oldStartValue, final String newStartValue) {
        replace(config, sectionName,
                value -> value.startsWith(oldStartValue + " "),
                value -> newStartValue + value.substring(oldStartValue.length()));
    }

    /**
     * Gets the section from the config and replaces the old value with the new value.
     *
     * @param config      the config
     * @param sectionName the section name
     * @param typeName    the type name
     * @param oldValue    the old value
     * @param newValue    the new value
     */
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    default void replaceValueInSection(final ConfigurationSection config, final String sectionName,
                                       final String typeName, final String oldValue, final String newValue) {
        replace(config, sectionName,
                value -> value.startsWith(typeName + " ") && value.contains(oldValue),
                value -> value.replace(oldValue, newValue));
    }

    /**
     * Gets the section from the config filters the values and applies the operation to the value.
     *
     * @param config      the config
     * @param sectionName the section name
     * @param filter      the filter to apply to the value
     * @param operation   the operation to apply to the value
     */
    default void replace(final ConfigurationSection config, final String sectionName,
                         final Function<String, Boolean> filter, final Function<String, String> operation) {
        final ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && filter.apply(value)) {
                section.set(key, operation.apply(value));
            }
        }
    }
}
