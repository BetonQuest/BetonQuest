package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This delegator is called for all set operations in a {@link ConfigurationSection}.
 */
public interface DelegateModificationConfigurationSection {
    /**
     * Delegate the {@link ConfigurationSection#set(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path of the value
     * @param value   The value to set
     */
    void set(@NotNull ConfigurationSection section, @NotNull final String path, @Nullable final Object value);

    /**
     * Delegate the {@link ConfigurationSection#addDefault(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path of the value
     * @param value   The value to add
     */
    void addDefault(@NotNull ConfigurationSection section, @NotNull String path, @Nullable Object value);

    /**
     * Delegate the {@link ConfigurationSection#createSection(String)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path to the section
     * @return The created section
     */
    @NotNull
    ConfigurationSection createSection(@NotNull ConfigurationSection section, @NotNull final String path);

    /**
     * Delegate the {@link ConfigurationSection#set(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path to the section
     * @param map     The values to set
     * @return The created section
     */
    @NotNull
    ConfigurationSection createSection(@NotNull ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map);
}
