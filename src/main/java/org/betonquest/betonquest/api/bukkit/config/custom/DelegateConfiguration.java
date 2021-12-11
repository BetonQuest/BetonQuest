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
public class DelegateConfiguration extends DelegateConfigurationSection implements Configuration {
    /**
     * The {@link ConfigurationSectionDecorator#delegate} but as {@link Configuration}.
     */
    protected final Configuration delegate;

    /**
     * The {@link DelegateModificationConfiguration} instance
     */
    private final DelegateModificationConfiguration setter;

    /**
     * Create a new delegate set instance.
     *
     * @param delegate The original {@link Configuration} that should be delegated on set
     * @param setter   The {@link DelegateModificationConfiguration} that manage modifications
     */
    public DelegateConfiguration(final Configuration delegate, final DelegateModificationConfiguration setter) {
        super(delegate, setter);
        this.setter = setter;
        this.delegate = delegate;
    }

    @Override
    public void addDefault(@NotNull final String path, @Nullable final Object value) {
        setter.addDefault(delegate, path, value);
    }

    @Override
    public void addDefaults(@NotNull final Map<String, Object> defaults) {
        setter.addDefaults(delegate, defaults);
    }

    @Override
    public void addDefaults(@NotNull final Configuration defaults) {
        setter.addDefaults(delegate, defaults);
    }

    @Override
    public @Nullable
    Configuration getDefaults() {
        return delegate.getDefaults();
    }

    @Override
    public void setDefaults(@NotNull final Configuration defaults) {
        setter.setDefaults(delegate, defaults);
    }

    @Override
    public @NotNull
    ConfigurationOptions options() {
        return delegate.options();
    }
}
