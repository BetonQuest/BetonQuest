package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * This is an interface to manage a {@link ConfigurationSection} that can be saved to multiple files.
 */
public interface MultiConfiguration extends Configuration {

    /**
     * Returns if a save is needed on a {@link ConfigurationSection} or an unassociated entry.
     *
     * @return true, if a save is needed
     */
    boolean needSave();

    /**
     * Gets all {@link ConfigurationSection}s that are unsaved.
     * Make sure to call {@link MultiConfiguration#markAsSaved(ConfigurationSection)}s
     * if you managed to successfully save a config.
     *
     * @return a set of all unsaved {@link ConfigurationSection}s
     */
    Set<ConfigurationSection> getUnsavedConfigs();

    /**
     * Marks the given {@link ConfigurationSection} as saved.
     *
     * @param section the {@link ConfigurationSection} to save
     * @return true, if it was marked as saved
     */
    boolean markAsSaved(ConfigurationSection section);

    /**
     * Gets the configuration of a specified path. The path can also be a configuration section.
     * <p>
     * If the path is not set in this {@link MultiConfiguration} this will return null.
     * This is also the case for default values.
     * <p>
     * If the path is a configuration section, it will be checked
     * that every entry in the configuration section is from the same source configuration section.
     * Otherwise, an {@link InvalidConfigurationException} is thrown.
     *
     * @param path The path of the entry to get the {@link ConfigurationSection} to
     * @return The clearly {@link ConfigurationSection} of the given path
     * @throws InvalidConfigurationException if the given path is defined in multiple configuration
     */
    @Nullable
    ConfigurationSection getSourceConfigurationSection(String path) throws InvalidConfigurationException;

    /**
     * Gets all keys, that are not associated with a {@link ConfigurationSection}.
     *
     * @return a list of unassociated keys.
     */
    List<String> getUnassociatedKeys();

    /**
     * All entries that are not associated with a {@link ConfigurationSection}
     * will be associated with the given config.
     *
     * @param targetConfig the config to associate entries to
     */
    void associateWith(ConfigurationSection targetConfig);

    /**
     * All entries under the given path that are not associated with a {@link ConfigurationSection}
     * will be associated with the given config.
     *
     * @param path         the path that should be associated with the given config
     * @param targetConfig the config to associate entries to
     */
    void associateWith(String path, ConfigurationSection targetConfig);
}
