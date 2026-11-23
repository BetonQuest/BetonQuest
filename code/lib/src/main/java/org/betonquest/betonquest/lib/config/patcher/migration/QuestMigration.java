package org.betonquest.betonquest.lib.config.patcher.migration;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.function.Function;

/**
 * Migrates a {@link Quest}.
 */
@FunctionalInterface
@SuppressWarnings("PMD.TooManyMethods")
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
     * <p>
     * Also replaces values in subsections.
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
     * <p>
     * Also replaces values in subsections.
     *
     * @param config      the config
     * @param sectionName the section name
     * @param typeName    the type name
     * @param oldValue    the old value
     * @param newValue    the new value
     */
    default void replaceValueInSection(final ConfigurationSection config, final String sectionName,
                                       final String typeName, final String oldValue, final String newValue) {
        replace(config, sectionName,
                value -> value.startsWith(typeName + " ") && value.contains(oldValue),
                value -> value.replace(oldValue, newValue));
    }

    /**
     * Gets the section from the config filters the values and applies the operation to the value.
     * <p>
     * Also replaces values in subsections.
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
        for (final String key : section.getKeys(true)) {
            if (section.isConfigurationSection(key)) {
                continue;
            }
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
        renameSectionChildren(config, oldSection, newSection, oldPath, newPath);

        config.set(oldPath, null);
    }

    private void renameSectionChildren(final MultiConfiguration config,
                                       final ConfigurationSection oldSection, final ConfigurationSection newSection,
                                       final String oldPath, final String newPath) throws InvalidConfigurationException {
        for (final String key : oldSection.getKeys(false)) {
            final ConfigurationSection keySection = oldSection.getConfigurationSection(key);
            if (keySection != null) {
                renameSectionChildren(config, keySection, newSection.createSection(key), oldPath + "." + key, newPath + "." + key);
                continue;
            }
            final ConfigurationSection source = config.getSourceConfigurationSection(oldPath + "." + key);
            if (source == null) {
                throw new InvalidConfigurationException("Cannot migrate '" + oldPath + "' to '" + newPath + "' for key: " + key);
            }
            newSection.set(key, oldSection.get(key));
            newSection.setComments(key, oldSection.getComments(key));
            newSection.setInlineComments(key, oldSection.getInlineComments(key));
            config.associateWith(newPath + "." + key, source);
            copyParentComments(source, oldPath, newPath);
        }
    }

    private void copyParentComments(final ConfigurationSection source, final String oldPath, final String newPath) {
        source.setComments(newPath, source.getComments(oldPath));
        source.setInlineComments(newPath, source.getInlineComments(oldPath));
        if (oldPath.contains(".") && newPath.contains(".")) {
            copyParentComments(source, oldPath.substring(0, oldPath.lastIndexOf('.')), newPath.substring(0, newPath.lastIndexOf('.')));
        }
    }

    /**
     * Expands a string value to a subsection with numbered keys.
     * <p>
     * Recursively scans the given section and all its subsections for keys whose string value
     * starts with the specified prefix, removes the prefix, parses the remaining content into a
     * list of sub-values using the provided parser function, and replaces the original key with a
     * subsection containing numbered entries. The source configuration is preserved for
     * all new entries.
     * <p>
     * If the parsed list is empty, the key is simply removed.
     *
     * @param config the multi-configuration to modify
     * @param path   the section path to scan for matching keys
     * @param prefix the string prefix that marks a key for expansion
     * @param parser function that converts the content without the prefix into a list of sub-values
     * @throws InvalidConfigurationException if the source configuration section cannot be found
     */
    default void expandToSubSection(final MultiConfiguration config, final String path,
                                    final String prefix,
                                    final Function<String, List<String>> parser) throws InvalidConfigurationException {
        final ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            final String fullPath = path + "." + key;
            if (section.isConfigurationSection(key)) {
                expandToSubSection(config, fullPath, prefix, parser);
                continue;
            }
            final String value = section.getString(key);
            if (value == null || !value.startsWith(prefix)) {
                continue;
            }
            replaceWithSubSection(config, fullPath, value.substring(prefix.length()).trim(), parser);
        }
    }

    private void replaceWithSubSection(final MultiConfiguration config, final String fullPath,
                                       final String content,
                                       final Function<String, List<String>> parser) throws InvalidConfigurationException {
        final List<String> subValues = parser.apply(content);
        if (subValues.isEmpty()) {
            config.set(fullPath, null);
            return;
        }
        final ConfigurationSection source = config.getSourceConfigurationSection(fullPath);
        if (source == null) {
            throw new InvalidConfigurationException("Cannot expand key '" + fullPath + "' to sub-section");
        }
        final List<String> comments = source.getComments(fullPath);
        final List<String> inlineComments = source.getInlineComments(fullPath);
        config.set(fullPath, null);
        for (int i = 0; i < subValues.size(); i++) {
            final String subKey = String.valueOf(i + 1);
            config.set(fullPath + "." + subKey, subValues.get(i));
            config.setComments(fullPath + "." + subKey, null);
            config.setInlineComments(fullPath + "." + subKey, null);
            config.associateWith(fullPath + "." + subKey, source);
        }
        source.setComments(fullPath, comments);
        source.setInlineComments(fullPath, inlineComments);
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
            final ConfigurationSection source = root.getSourceConfigurationSection(section.getCurrentPath() + "." + oldKey);
            if (source == null) {
                throw new InvalidConfigurationException("Cannot migrate key '" + oldKey + "' to '" + newKey + "'");
            }
            section.set(newKey, value);
            section.setComments(newKey, section.getComments(oldKey));
            section.setInlineComments(newKey, section.getInlineComments(oldKey));
            section.set(oldKey, null);
            root.associateWith(section.getCurrentPath() + "." + newKey, source);
        }
    }
}
