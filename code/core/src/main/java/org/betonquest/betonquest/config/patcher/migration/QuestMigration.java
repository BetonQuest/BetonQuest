package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
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

    /**
     * Renames a configuration section in the config.
     *
     * @param config  the source config
     * @param oldPath the section path to rename
     * @param newPath the new name of the section
     * @throws InvalidConfigurationException if an error occurs
     */
    default void renameSection(final MultiConfiguration config, final String oldPath, final String newPath) throws InvalidConfigurationException {
        final ConfigurationSection oldSection = config.getConfigurationSection(oldPath);
        if (oldSection == null) {
            return;
        }
        final ConfigurationSection newSection = config.createSection(newPath);
        for (final String key : oldSection.getKeys(false)) {
            final ConfigurationSection source = config.getSourceConfigurationSection(oldPath + "." + key);
            if (source == null) {
                throw new InvalidConfigurationException("Cannot migrate '" + oldPath + "' to '" + newPath + "' for key: " + key);
            }
            newSection.set(key, oldSection.get(key));
            newSection.setComments(key, oldSection.getComments(key));
            newSection.setInlineComments(key, oldSection.getInlineComments(key));
            config.associateWith(newPath + "." + key, source);
        }
        config.set(oldPath, null);
    }

    /**
     * Renames a key in all subsections of the given path configuration key in a specific section.
     *
     * @param root   the source configuration of the path and associate with
     * @param path   the base path of the sections to replace the keys in
     * @param oldKey the old to replace key
     * @param newKey the new key
     * @throws InvalidConfigurationException if an error occurs
     * @see #replaceKeyInSection(MultiConfiguration, ConfigurationSection, String, String)
     */
    default void replaceKeyInSections(final MultiConfiguration root, final String path, final String oldKey, final String newKey)
            throws InvalidConfigurationException {
        final ConfigurationSection config = root.getConfigurationSection(path);
        if (config == null) {
            return;
        }
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) {
                continue;
            }
            replaceKeyInSection(root, section, oldKey, newKey);
        }
    }

    /**
     * Renames a configuration key in a specific section.
     *
     * @param root    the source configuration to associate with
     * @param section the section to replace the key in
     * @param oldKey  the old to replace key
     * @param newKey  the new key
     * @throws InvalidConfigurationException if an error occurs
     */
    default void replaceKeyInSection(final MultiConfiguration root, final ConfigurationSection section,
                                     final String oldKey, final String newKey) throws InvalidConfigurationException {
        final String value = section.getString(oldKey);
        if (value != null) {
            final ConfigurationSection sourceConfigurationSection = root.getSourceConfigurationSection(section.getCurrentPath() + "." + oldKey);
            section.set(newKey, value);
            section.set(oldKey, null);
            if (sourceConfigurationSection != null) {
                root.associateWith(section.getCurrentPath() + "." + newKey, sourceConfigurationSection);
            }
        }
    }
}
