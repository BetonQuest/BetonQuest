package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator},
 * that delegate the {@link Configuration} methods that modify something.
 */
public class DelegateSetConfiguration extends DelegateSetConfigurationSection implements Configuration {
    /**
     * The {@link ConfigurationSectionDecorator#delegate} but as {@link Configuration}.
     */
    protected final Configuration delegate;

    /**
     * Create a new delegate set instance.
     *
     * @param delegate The original {@link Configuration} that should be delegated on set
     * @param setter The {@link DelegateSet} that manage modifications
     */
    public DelegateSetConfiguration(final Configuration delegate, final DelegateSet setter) {
        super(delegate, setter);
        this.delegate = delegate;
    }

    @Override
    public void addDefaults(@NotNull final Map<String, Object> defaults) {
        delegate.addDefaults(defaults);
    }

    @Override
    public void addDefaults(@NotNull final Configuration defaults) {
        delegate.addDefaults(defaults);
    }

    @Override
    public @Nullable
    Configuration getDefaults() {
        return delegate.getDefaults();
    }

    @Override
    public void setDefaults(@NotNull final Configuration defaults) {
        delegate.setDefaults(defaults);
    }

    @Override
    public @NotNull
    ConfigurationOptions options() {
        return delegate.options();
    }
}
