package org.betonquest.betonquest.api.bukkit.config.custom.lazy;

import org.betonquest.betonquest.api.bukkit.config.custom.handle.ConfigurationSectionModificationHandler;
import org.betonquest.betonquest.api.bukkit.config.custom.handle.HandleModificationConfigurationSection;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * This ConfigurationSection operates on a parent section, where every read operation does not create the child section.
 * A write operation will create the child section if it does not exist.
 */
public class LazyConfigurationSection extends HandleModificationConfigurationSection {
    /**
     * Creates a new handler instance.
     *
     * @param originalParent The original parent {@link Configuration} in which modifications will be handled.
     * @param childPath      The path of the child section.
     */
    public LazyConfigurationSection(final ConfigurationSection originalParent, final String childPath) {
        super(new LazyMemoryConfigurationSection(originalParent, childPath), new ConfigurationSectionModificationHandler() {
            @Override
            public void set(final ConfigurationSection section, final String path, @Nullable final Object value) {
                originalParent.set(childPath, section);
                section.set(path, value);
            }

            @Override
            public void addDefault(final ConfigurationSection section, final String path, @Nullable final Object value) {
                originalParent.set(childPath, section);
                section.addDefault(path, value);
            }

            @Override
            public ConfigurationSection createSection(final ConfigurationSection section, final String path) {
                originalParent.set(childPath, section);
                return section.createSection(path);
            }

            @Override
            public ConfigurationSection createSection(final ConfigurationSection section, final String path, final Map<?, ?> map) {
                originalParent.set(childPath, section);
                return section.createSection(path, map);
            }

            @Override
            public void setComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
                originalParent.set(childPath, section);
                section.setComments(path, comments);
            }

            @Override
            public void setInlineComments(final ConfigurationSection section, final String path, @Nullable final List<String> comments) {
                originalParent.set(childPath, section);
                section.setInlineComments(path, comments);
            }
        });
    }

    @Override
    @Contract("null -> null; !null -> !null")
    @Nullable
    protected Object wrapModifiable(@Nullable final Object obj) {
        return obj;
    }

    /**
     * This ConfigurationSection operates on a parent section,
     * where every read operation does not create the child section.
     */
    public static class LazyMemoryConfigurationSection extends MemorySection {
        /**
         * Creates a new MemorySection.
         *
         * @param originalParent The original parent {@link Configuration} in which modifications will be handled.
         * @param childPath      The path of the child section.
         */
        public LazyMemoryConfigurationSection(final ConfigurationSection originalParent, final String childPath) {
            super(originalParent, childPath);
        }
    }
}
