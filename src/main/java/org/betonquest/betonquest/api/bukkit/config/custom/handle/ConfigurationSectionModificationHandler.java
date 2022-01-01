package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * This handler is called for all modification operations in a {@link ConfigurationSection}.
 */
public interface ConfigurationSectionModificationHandler {
    /**
     * Handles the {@link ConfigurationSection#set(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path of the value
     * @param value   The value to set
     */
    void set(@NotNull ConfigurationSection section, @NotNull final String path, @Nullable final Object value);

    /**
     * Handles the {@link ConfigurationSection#addDefault(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path of the value
     * @param value   The value to add
     */
    void addDefault(@NotNull ConfigurationSection section, @NotNull String path, @Nullable Object value);

    /**
     * Handles the {@link ConfigurationSection#createSection(String)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path to the section
     * @return The created section
     */
    @NotNull
    ConfigurationSection createSection(@NotNull ConfigurationSection section, @NotNull final String path);

    /**
     * Handles the {@link ConfigurationSection#set(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path to the section
     * @param map     The values to set
     * @return The created section
     */
    @NotNull
    ConfigurationSection createSection(@NotNull ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map);

    /**
     * Handles the {@link ConfigurationSection#setComments(String, List)} method.
     *
     * @param section  The {@link ConfigurationSection} from which the method was called
     * @param path     The path to the section
     * @param comments The comments to set
     */
    void setComments(@NotNull ConfigurationSection section, @NotNull final String path, @Nullable final List<String> comments);

    /**
     * Handles the {@link ConfigurationSection#setInlineComments(String, List)} method.
     *
     * @param section  The {@link ConfigurationSection} from which the method was called
     * @param path     The path to the section
     * @param comments The comments to set
     */
    void setInlineComments(@NotNull ConfigurationSection section, @NotNull final String path, @Nullable final List<String> comments);
}
