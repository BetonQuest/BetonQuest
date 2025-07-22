package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;

/**
 * This {@link ConfigurationOptions} implementation guarantees that it is not possible to break out of the
 * {@link HandleModificationConfiguration} instance.
 */
public class HandleConfigurationOptions extends ConfigurationOptions {

    /**
     * The original {@link ConfigurationOptions} hidden behind this decorator.
     */
    private final ConfigurationOptions original;

    /**
     * Creates a new {@link ConfigurationOptions} instance, that maps to the original one.
     *
     * @param configuration The {@link Configuration} instance that should be returned by the configuration method
     * @param original      The original {@link Configuration}, to apply the options to
     */
    protected HandleConfigurationOptions(final Configuration configuration, final ConfigurationOptions original) {
        super(configuration);
        this.original = original;
    }

    @Override
    public char pathSeparator() {
        return original.pathSeparator();
    }

    @Override
    public ConfigurationOptions pathSeparator(final char value) {
        original.pathSeparator(value);
        return this;
    }

    @Override
    public boolean copyDefaults() {
        return original.copyDefaults();
    }

    @Override
    public ConfigurationOptions copyDefaults(final boolean value) {
        original.copyDefaults(value);
        return this;
    }
}
