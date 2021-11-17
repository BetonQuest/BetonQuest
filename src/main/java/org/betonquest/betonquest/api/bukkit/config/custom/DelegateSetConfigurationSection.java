package org.betonquest.betonquest.api.bukkit.config.custom;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DelegateSetConfigurationSection extends ConfigurationSectionDecorator {

    private final DelegateSet setter;

    public DelegateSetConfigurationSection(final ConfigurationSection delegate, final DelegateSet setter) {
        super(delegate);
        this.setter = setter;
    }

    @Override
    public @Nullable
    Configuration getRoot() {
        return new DelegateSetConfiguration(delegate.getRoot(), setter);
    }

    @Override
    public @Nullable
    ConfigurationSection getParent() {
        return new DelegateSetConfigurationSection(delegate.getParent(), setter);
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path) {
        return wrapModifiable(delegate.get(path));
    }

    @Override
    public @Nullable
    Object get(@NotNull final String path, @Nullable final Object def) {
        return wrapModifiable(delegate.get(path));
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
        delegate.addDefault(path, value);
    }

    private Object wrapModifiable(final Object obj) {
        if (obj instanceof DelegateSetConfigurationSection) {
            return obj;
        }
        if (obj instanceof Configuration) {
            return new DelegateSetConfiguration((Configuration) obj, setter);
        }
        if (obj instanceof ConfigurationSection) {
            return new DelegateSetConfigurationSection((ConfigurationSection) obj, setter);
        }
        return obj;
    }
}
