package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
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
     * The {@link ConfigurationSectionDecorator#original} but as {@link Configuration}.
     */
    protected final Configuration original;

    /**
     * Creates a new unmodifiable instance.
     *
     * @param original The original {@link ConfigurationSection} that should be unmodifiable.
     */
    public UnmodifiableConfiguration(final Configuration original) {
        super(original);
        this.original = original;
    }

    @Override
    public void addDefaults(@NotNull final Map<String, Object> defaults) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public void addDefaults(@NotNull final Configuration defaults) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public @Nullable
    Configuration getDefaults() {
        return new UnmodifiableConfiguration(original.getDefaults());
    }

    @Override
    public void setDefaults(@NotNull final Configuration defaults) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public @NotNull
    ConfigurationOptions options() {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE + " and options are not implemented");
    }
}
