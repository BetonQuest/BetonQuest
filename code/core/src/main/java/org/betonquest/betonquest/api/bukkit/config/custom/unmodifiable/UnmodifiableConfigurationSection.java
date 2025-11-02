package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.custom.ConfigurationSectionDecorator;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationSectionModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfigurationSection;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * This is an extension of {@link ConfigurationSectionDecorator},
 * that makes the {@link ConfigurationSection} unmodifiable.
 */
public class UnmodifiableConfigurationSection extends HandleModificationConfigurationSection {

    /**
     * Creates a new unmodifiable instance.
     *
     * @param original The original {@link ConfigurationSection} that should be unmodifiable.
     */
    public UnmodifiableConfigurationSection(final ConfigurationSection original) {
        super(original, new ConfigurationSectionModificationHandler() {
            @Override
            public void addDefault(final ConfigurationSection section, final String path, @Nullable final Object value) {
                throw new UnsupportedOperationException(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void set(final ConfigurationSection section, final String path, @Nullable final Object value) {
                throw new UnsupportedOperationException(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE);
            }

            @Override
            public ConfigurationSection createSection(final ConfigurationSection section, final String path) {
                throw new UnsupportedOperationException(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE);
            }

            @Override
            public ConfigurationSection createSection(final ConfigurationSection section, final String path, final Map<?, ?> map) {
                throw new UnsupportedOperationException(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
                throw new UnsupportedOperationException(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE);
            }

            @Override
            public void setInlineComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
                throw new UnsupportedOperationException(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE);
            }
        });
    }

    @Contract("null -> null; !null -> !null")
    @Nullable
    static /* default */ Object getObject(@Nullable final Object obj) {
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

    @Override
    @Contract("null -> null; !null -> !null")
    @Nullable
    protected Object wrapModifiable(@Nullable final Object obj) {
        return getObject(obj);
    }
}
