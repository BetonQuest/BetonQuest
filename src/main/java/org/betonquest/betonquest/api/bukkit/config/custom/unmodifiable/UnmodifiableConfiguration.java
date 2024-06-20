package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator}, that makes the {@link Configuration} unmodifiable.
 */
public class UnmodifiableConfiguration extends HandleModificationConfiguration {
    /**
     * Exception message for unmodifiable behaviours.
     */
    public static final String UNMODIFIABLE_MESSAGE = "This config is unmodifiable";

    /**
     * Creates a new unmodifiable instance.
     *
     * @param original The original {@link Configuration} that should be unmodifiable.
     */
    public UnmodifiableConfiguration(final Configuration original) {
        super(original, new ConfigurationModificationHandler() {
            @Override
            public void addDefault(final ConfigurationSection section, final String path, @Nullable final Object value) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void addDefaults(final Configuration section, final Map<String, Object> defaults) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void addDefaults(final Configuration section, final Configuration defaults) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setDefaults(final Configuration section, final Configuration defaults) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void set(final ConfigurationSection section, final String path, @Nullable final Object value) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public ConfigurationSection createSection(final ConfigurationSection section, final String path) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public ConfigurationSection createSection(final ConfigurationSection section, final String path, final Map<?, ?> map) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setInlineComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }
        });
    }

    @Override
    @Contract("null -> null; !null -> !null")
    @Nullable
    protected Object wrapModifiable(@Nullable final Object obj) {
        return UnmodifiableConfigurationSection.getObject(obj);
    }
}
