package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class UnmodifiableConfiguration extends UnmodifiableConfigurationSection implements Configuration {

    protected final Configuration delegate;

    public UnmodifiableConfiguration(final Configuration delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void addDefaults(@NotNull final Map<String, Object> defaults) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    @Override
    public void addDefaults(@NotNull final Configuration defaults) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    @Override
    public @Nullable
    Configuration getDefaults() {
        return delegate.getDefaults();
    }

    @Override
    public void setDefaults(@NotNull final Configuration defaults) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    @Override
    public @NotNull
    ConfigurationOptions options() {
        throw new UnsupportedOperationException("This config is unmodifiable and options are not implemented");
    }
}
