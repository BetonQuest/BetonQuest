package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
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
    protected final ConfigurationModificationHandler handler;

    /**
     * Creates a new handler instance.
     *
     * @param original The original {@link Configuration} in which modifications will be handled
     * @param handler  The {@link ConfigurationModificationHandler} that handles modifications
     */
    public HandleModificationConfiguration(final Configuration original, final ConfigurationModificationHandler handler) {
        super(original, handler);
        this.handler = handler;
        this.original = original;
    }

    @Override
    public void addDefault(final String path, @Nullable final Object value) {
        handler.addDefault(original, path, value);
    }

    @Override
    public void addDefaults(final Map<String, Object> defaults) {
        handler.addDefaults(original, defaults);
    }

    @Override
    public void addDefaults(final Configuration defaults) {
        handler.addDefaults(original, defaults);
    }

    @Override
    @Nullable
    public Configuration getDefaults() {
        return (Configuration) wrapModifiable(original.getDefaults());
    }

    @Override
    public void setDefaults(final Configuration defaults) {
        handler.setDefaults(original, defaults);
    }

    @Override
    public ConfigurationOptions options() {
        return new HandleConfigurationOptions(this, original.options());
    }
}
