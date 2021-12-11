package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator},
 * that delegate the {@link ConfigurationSection} methods that modify something.
 */
public class DelegateConfigurationSection extends ConfigurationSectionDecorator {

    /**
     * The {@link DelegateModificationConfigurationSection} instance
     */
    private final DelegateModificationConfigurationSection setter;

    /**
     * Create a new delegate set instance.
     *
     * @param delegate The original {@link ConfigurationSection} that should be delegated on set
     * @param setter   The {@link DelegateModificationConfigurationSection} that manage modifications
     */
    public DelegateConfigurationSection(final ConfigurationSection delegate, final DelegateModificationConfigurationSection setter) {
        super(delegate);
        this.setter = setter;
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return new DelegateConfiguration(delegate.getRoot(), (DelegateModificationConfiguration) setter);
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return new DelegateConfigurationSection(delegate.getParent(), setter);
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
        setter.set(delegate, path, value);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path) {
        return setter.createSection(delegate, path);
    }

    @Override
    public @NotNull
    ConfigurationSection createSection(@NotNull final String path, @NotNull final Map<?, ?> map) {
        return setter.createSection(delegate, path, map);
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
        setter.addDefault(delegate, path, value);
    }

    private Object wrapModifiable(final Object obj) {
        if (obj instanceof DelegateConfigurationSection) {
            return obj;
        }
        if (obj instanceof Configuration) {
            return new DelegateConfiguration((Configuration) obj, (DelegateModificationConfiguration) setter);
        }
        if (obj instanceof ConfigurationSection) {
            return new DelegateConfigurationSection((ConfigurationSection) obj, setter);
        }
        return obj;
    }
}
