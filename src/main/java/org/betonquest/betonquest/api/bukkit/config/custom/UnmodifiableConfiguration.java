package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator}, that makes the {@link Configuration} unmodifiable.
 */
public class UnmodifiableConfiguration extends UnmodifiableConfigurationSection implements Configuration {
    /**
     * The {@link ConfigurationSectionDecorator#delegate} but as {@link Configuration}.
     */
    protected final Configuration delegate;

    /**
     * Create a new unmodifiable instance.
     * @param delegate The original {@link ConfigurationSection} that should be unmodifiable.
     */
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
        return new UnmodifiableConfiguration(delegate.getDefaults());
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
