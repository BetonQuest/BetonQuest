package org.betonquest.betonquest.api.bukkit.config.util;

import org.betonquest.betonquest.api.bukkit.config.custom.DelegateSet;
import org.betonquest.betonquest.api.bukkit.config.custom.DelegateSetConfigurationSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DelegateSetToConfigurationSection implements DelegateSet {
    private final ConfigurationSection section;

    public DelegateSetToConfigurationSection() {
        section = new MemoryConfiguration();
    }

    @Override
    public void set(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
        final String sectionPath = (section.getCurrentPath() == null || section.getCurrentPath().isEmpty() ? "" : section.getCurrentPath() + ".") + path;
        this.section.set(sectionPath, value);
        section.set(path, value);
    }

    @Override
    public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
        return new DelegateSetConfigurationSection(section.createSection(path), this);
    }

    @Override
    public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map) {
        return new DelegateSetConfigurationSection(section.createSection(path, map), this);
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
