package org.betonquest.betonquest.api.bukkit.config.util;

import org.betonquest.betonquest.api.bukkit.config.custom.DelegateConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.DelegateModificationConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Delegate all modifications to another {@link Configuration}.
 */
public class DelegateModificationToConfiguration implements DelegateModificationConfiguration {
    /**
     * The {@link Configuration} where to store all modifications in.
     */
    private final Configuration section;

    /**
     * Empty constructor
     */
    public DelegateModificationToConfiguration() {
        section = new MemoryConfiguration();
    }

    @Override
    public void set(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
        this.section.set(getAbsolutePath(section, path), value);
        section.set(path, value);
    }

    @Override
    public void addDefault(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
        this.section.addDefault(getAbsolutePath(section, path), value);
        section.addDefault(path, value);
    }

    @Override
    public void addDefaults(@NotNull final Configuration section, @NotNull final Map<String, Object> defaults) {
        this.section.addDefaults(defaults);
        section.addDefaults(defaults);
    }

    @Override
    public void addDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {
        this.section.addDefaults(defaults);
        section.addDefaults(defaults);
    }

    @Override
    public void setDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {
        this.section.setDefaults(defaults);
        section.setDefaults(defaults);
    }

    @NotNull
    @Override
    public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
        this.section.createSection(getAbsolutePath(section, path));
        return new DelegateConfigurationSection(section.createSection(path), this);
    }

    @NotNull
    @Override
    public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map) {
        this.section.createSection(getAbsolutePath(section, path), map);
        return new DelegateConfigurationSection(section.createSection(path, map), this);
    }

    private String getAbsolutePath(final ConfigurationSection section, final String path) {
        if (section.getCurrentPath() == null || section.getCurrentPath().isEmpty()) {
            return path;
        }
        return section.getCurrentPath() + "." + path;
    }

    /**
     * Get the {@link Configuration} with all modifications.
     *
     * @return the {@link Configuration}
     */
    public Configuration getSection() {
        return section;
    }
}
