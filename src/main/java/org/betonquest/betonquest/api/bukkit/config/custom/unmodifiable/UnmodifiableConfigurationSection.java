package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
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
     * Exception message for unmodifiable behaviours.
     */
    public static final String UNMODIFIABLE_MESSAGE = "This config is unmodifiable";

    /**
     * Creates a new unmodifiable instance.
     *
     * @param original The original {@link ConfigurationSection} that should be unmodifiable.
     */
    public UnmodifiableConfigurationSection(final ConfigurationSection original) {
        super(original);
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return new UnmodifiableConfiguration(original.getRoot());
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return original.getParent() == null ? null : new UnmodifiableConfigurationSection(original.getParent());
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        return wrapModifiable(original.get(path));
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        return wrapModifiable(original.get(path, def));
    }

    @Override
    public void set(@NotNull final String path, @Nullable final Object value) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz) {
        return clazz.cast(wrapModifiable(original.getObject(path, clazz)));
    }

    @Override
    public <T> T getObject(@NotNull final String path, @NotNull final Class<T> clazz, @Nullable final T def) {
        return clazz.cast(wrapModifiable(original.getObject(path, clazz, def)));
    }

    @Override
    public @Nullable
    ConfigurationSection getConfigurationSection(@NotNull final String path) {
        return (ConfigurationSection) wrapModifiable(original.getConfigurationSection(path));
    }

    @Override
    public @Nullable
    ConfigurationSection getDefaultSection() {
        return (ConfigurationSection) wrapModifiable(original.getDefaultSection());
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
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
