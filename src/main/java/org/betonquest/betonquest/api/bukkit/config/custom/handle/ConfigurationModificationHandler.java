package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This handler is called for all modification operations in a {@link Configuration}.
 */
public interface ConfigurationModificationHandler extends ConfigurationSectionModificationHandler {
    /**
     * Handles the {@link Configuration#addDefault(String, Object)} method.
     *
     * @param section The {@link Configuration} from which the method was called
     * @param path    The path of the value
     * @param value   The value to add
     */
    @Override
    void addDefault(@NotNull ConfigurationSection section, @NotNull String path, @Nullable Object value);

    /**
     * Handles the {@link Configuration#addDefaults(Map)} method.
     *
     * @param section  The {@link Configuration} from which the method was called
     * @param defaults The values to add
     */
    void addDefaults(@NotNull Configuration section, @NotNull Map<String, Object> defaults);

    /**
     * Handles the {@link Configuration#addDefaults(Configuration)} method.
     *
     * @param section  The {@link Configuration} from which the method was called
     * @param defaults The values to add
     */
    void addDefaults(@NotNull Configuration section, @NotNull Configuration defaults);

    /**
     * Handles the {@link Configuration#setDefaults(Configuration)} method.
     *
     * @param section  The {@link Configuration} from which the method was called
     * @param defaults The values to set
     */
    void setDefaults(@NotNull Configuration section, @NotNull Configuration defaults);
}
