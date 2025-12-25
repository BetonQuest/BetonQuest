package org.betonquest.betonquest.api.bukkit.config.custom.handle.util;

import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfigurationSection;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationSectionModificationHandler.getAbsolutePath;

/**
 * Copies all modifications to the target {@link Configuration} to another Configuration.
 */
public class HandleModificationToConfiguration implements ConfigurationModificationHandler {

    /**
     * The {@link Configuration} that stores all modifications.
     */
    private final Configuration section;

    /**
     * The map that stores all block comment modifications.
     */
    private final Map<String, List<String>> comments;

    /**
     * The map that stores all inline comment modifications.
     */
    private final Map<String, List<String>> inlineComments;

    /**
     * Empty constructor
     */
    public HandleModificationToConfiguration() {
        section = new MemoryConfiguration();
        comments = new HashMap<>();
        inlineComments = new HashMap<>();
    }

    @Override
    public void set(final ConfigurationSection section, final String path, @Nullable final Object value) {
        this.section.set(getAbsolutePath(section, path), value);
        section.set(path, value);
    }

    @Override
    public void addDefault(final ConfigurationSection section, final String path, @Nullable final Object value) {
        this.section.addDefault(getAbsolutePath(section, path), value);
        section.addDefault(path, value);
    }

    @Override
    public void addDefaults(final Configuration section, final Map<String, Object> defaults) {
        this.section.addDefaults(defaults);
        section.addDefaults(defaults);
    }

    @Override
    public void addDefaults(final Configuration section, final Configuration defaults) {
        this.section.addDefaults(defaults);
        section.addDefaults(defaults);
    }

    @Override
    public void setDefaults(final Configuration section, final Configuration defaults) {
        this.section.setDefaults(defaults);
        section.setDefaults(defaults);
    }

    @Override
    public ConfigurationSection createSection(final ConfigurationSection section, final String path) {
        this.section.createSection(getAbsolutePath(section, path));
        return new HandleModificationConfigurationSection(section.createSection(path), this);
    }

    @Override
    public ConfigurationSection createSection(final ConfigurationSection section, final String path, final Map<?, ?> map) {
        this.section.createSection(getAbsolutePath(section, path), map);
        return new HandleModificationConfigurationSection(section.createSection(path, map), this);
    }

    @Override
    public void setComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
        if (comments == null) {
            this.comments.remove(path);
        } else {
            this.comments.put(getAbsolutePath(section, path), comments);
        }
        section.setComments(path, comments);
    }

    @Override
    public void setInlineComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
        if (comments == null) {
            this.inlineComments.remove(path);
        } else {
            this.inlineComments.put(getAbsolutePath(section, path), comments);
        }
        section.setInlineComments(path, comments);
    }

    /**
     * Get the {@link Configuration} with all modifications.
     *
     * @return the {@link Configuration}
     */
    public Configuration getSection() {
        return section;
    }

    /**
     * Get the map with all block comment modifications.
     *
     * @return the map
     */
    public Map<String, List<String>> getComments() {
        return comments;
    }

    /**
     * Get the map with all inline comment modifications.
     *
     * @return the map
     */
    public Map<String, List<String>> getInlineComments() {
        return inlineComments;
    }
}
