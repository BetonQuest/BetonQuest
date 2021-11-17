package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface DelegateSet {
    void set(@NotNull ConfigurationSection section, @NotNull final String path, @Nullable final Object value);

    ConfigurationSection createSection(@NotNull ConfigurationSection section, @NotNull final String path);

    ConfigurationSection createSection(@NotNull ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map);
}
