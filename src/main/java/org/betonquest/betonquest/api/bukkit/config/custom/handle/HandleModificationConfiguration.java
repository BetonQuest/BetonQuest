package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator},
 * that handles the {@link Configuration} methods that modify something.
 */
public class HandleModificationConfiguration extends HandleModificationConfigurationSection implements Configuration {
    /**
     * The {@link ConfigurationSectionDecorator#original} but as {@link Configuration} instance
     */
    protected final Configuration original;

    /**
     * The {@link ConfigurationModificationHandler} instance
     */
    private final ConfigurationModificationHandler setter;

    /**
     * Creates a new handler instance.
     *
     * @param original The original {@link Configuration} in which modifications will be handled
     * @param setter   The {@link ConfigurationModificationHandler} that handles modifications
     */
    public HandleModificationConfiguration(final Configuration original, final ConfigurationModificationHandler setter) {
        super(original, setter);
        this.setter = setter;
        this.original = original;
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        setter.addDefault(original, path, value);
    }

    @Override
    public void addDefaults(@NotNull final Map<String, Object> defaults) {
        setter.addDefaults(original, defaults);
    }

    @Override
    public void addDefaults(@NotNull final Configuration defaults) {
        setter.addDefaults(original, defaults);
    }

    @Override
    public @Nullable
    Configuration getDefaults() {
        return original.getDefaults();
    }

    @Override
    public void setDefaults(@NotNull final Configuration defaults) {
        setter.setDefaults(original, defaults);
    }

    @Override
    public @NotNull
    ConfigurationOptions options() {
        return original.options();
    }
}
