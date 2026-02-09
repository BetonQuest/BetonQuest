package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * This is an extension of the {@link ConfigurationSectionDecorator},
 * that handles all {@link ConfigurationSection} methods that do modifications.
 */
public class HandleModificationConfigurationSection extends ConfigurationSectionDecorator {

    /**
     * The {@link ConfigurationSectionModificationHandler} instance.
     */
    protected final ConfigurationSectionModificationHandler handler;

    /**
     * Creates a new handler instance.
     *
     * @param original The original {@link ConfigurationSection} in which modifications will be handled
     * @param handler  The {@link ConfigurationSectionModificationHandler} that handles modifications
     */
    public HandleModificationConfigurationSection(final ConfigurationSection original, final ConfigurationSectionModificationHandler handler) {
        super(original);
        this.handler = handler;
    }

    @Override
    public Map<String, Object> getValues(final boolean deep) {
        final Map<String, Object> values = original.getValues(deep);
        values.replaceAll((k, v) -> wrapModifiable(v));
        return values;
    }

    @Override
    @Nullable
    public Configuration getRoot() {
        return (Configuration) wrapModifiable(original.getRoot());
    }

    @Override
    @Nullable
    public ConfigurationSection getParent() {
        return (ConfigurationSection) wrapModifiable(original.getParent());
    }

    @Override
    @Nullable
    public Object get(final String path) {
        return wrapModifiable(original.get(path));
    }

    @Override
    @Nullable
    public Object get(final String path, @Nullable final Object def) {
        return wrapModifiable(original.get(path, def));
    }

    @Override
    public void set(final String path, @Nullable final Object value) {
        handler.set(original, path, value);
    }

    @Override
    public ConfigurationSection createSection(final String path) {
        return handler.createSection(original, path);
    }

    @Override
    public ConfigurationSection createSection(final String path, final Map<?, ?> map) {
        return handler.createSection(original, path, map);
    }

    @Override
    public <T> T getObject(final String path, final Class<T> clazz) {
        return clazz.cast(wrapModifiable(original.getObject(path, clazz)));
    }

    @Override
    public <T> T getObject(final String path, final Class<T> clazz, @Nullable final T def) {
        return clazz.cast(wrapModifiable(original.getObject(path, clazz, def)));
    }

    @Override
    @Nullable
    public ConfigurationSection getConfigurationSection(final String path) {
        return (ConfigurationSection) wrapModifiable(original.getConfigurationSection(path));
    }

    @Override
    @Nullable
    public ConfigurationSection getDefaultSection() {
        return (ConfigurationSection) wrapModifiable(original.getDefaultSection());
    }

    @Override
    public void addDefault(final String path, @Nullable final Object value) {
        handler.addDefault(original, path, value);
    }

    @Override
    public void setComments(final String path, @Nullable final List<String> comments) {
        handler.setComments(original, path, comments);
    }

    @Override
    public void setInlineComments(final String path, @Nullable final List<String> comments) {
        handler.setInlineComments(original, path, comments);
    }

    /**
     * Wraps a given object into an instance of this class.
     *
     * @param obj the raw object
     * @return the wrapped object
     */
    @Contract("null -> null; !null -> !null")
    @Nullable
    protected Object wrapModifiable(@Nullable final Object obj) {
        if (obj instanceof HandleModificationConfigurationSection) {
            return obj;
        }
        if (obj instanceof Configuration) {
            return new HandleModificationConfiguration((Configuration) obj, (ConfigurationModificationHandler) handler);
        }
        if (obj instanceof ConfigurationSection) {
            return new HandleModificationConfigurationSection((ConfigurationSection) obj, handler);
        }
        return obj;
    }
}
