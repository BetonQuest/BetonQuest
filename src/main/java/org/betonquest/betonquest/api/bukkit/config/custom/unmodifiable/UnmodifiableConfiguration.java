package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
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
            public void addDefault(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void addDefaults(@NotNull final Configuration section, @NotNull final Map<String, Object> defaults) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void addDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setDefaults(@NotNull final Configuration section, @NotNull final Configuration defaults) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void set(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final Object value) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @NotNull
            @Override
            public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @NotNull
            @Override
            public ConfigurationSection createSection(@NotNull final ConfigurationSection section, @NotNull final String path, @NotNull final Map<?, ?> map) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setComments(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final List<String> comments) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setInlineComments(@NotNull final ConfigurationSection section, @NotNull final String path, @Nullable final List<String> comments) {
                throw new UnsupportedOperationException(UNMODIFIABLE_MESSAGE);
            }
        });
    }

    @Override
    protected Object wrapModifiable(final Object obj) {
        return UnmodifiableConfigurationSection.getObject(obj);
    }
}
