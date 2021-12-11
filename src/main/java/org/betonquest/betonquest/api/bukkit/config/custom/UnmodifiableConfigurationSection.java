package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator},
 * that makes the {@link ConfigurationSection} unmodifiable.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UnmodifiableConfigurationSection extends ConfigurationSectionDecorator {

    /**
     * Create a new unmodifiable instance.
     *
     * @param delegate The original {@link ConfigurationSection} that should be unmodifiable.
     */
    public UnmodifiableConfigurationSection(final ConfigurationSection delegate) {
        super(delegate);
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return new UnmodifiableConfiguration(delegate.getRoot());
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return new UnmodifiableConfigurationSection(delegate.getParent());
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        return wrapModifiable(delegate.get(path));
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        return wrapModifiable(delegate.get(path, def));
    }

    @Override
    public void set(@NotNull final String path, @Nullable final Object value) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
        return clazz.cast(wrapModifiable(delegate.getObject(path, clazz)));
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return clazz.cast(wrapModifiable(delegate.getObject(path, clazz, def)));
    }

    @Override
    public @Nullable
    ConfigurationSection getConfigurationSection(@NotNull final String path) {
        return (ConfigurationSection) wrapModifiable(delegate.getConfigurationSection(path));
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
        return (ConfigurationSection) wrapModifiable(delegate.getDefaultSection());
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        throw new UnsupportedOperationException("This config is unmodifiable");
    }

    private Object wrapModifiable(final Object obj) {
        if (obj instanceof UnmodifiableConfigurationSection) {
            return obj;
        }
        if (obj instanceof Configuration) {
            return new UnmodifiableConfiguration((Configuration) obj);
        }
        if (obj instanceof ConfigurationSection) {
            return new UnmodifiableConfigurationSection((ConfigurationSection) obj);
        }
        return obj;
    }
}
