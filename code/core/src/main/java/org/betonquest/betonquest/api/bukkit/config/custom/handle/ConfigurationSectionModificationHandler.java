package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * This handler is called for all modification operations in a {@link ConfigurationSection}.
 */
public interface ConfigurationSectionModificationHandler {
    /**
     * Get the absolut string path.
     * It concatenates the current section with the path.
     *
     * @param section the current {@link ConfigurationSection}
     * @param path    the target path
     * @return the concatenated absolut path
     */
    static String getAbsolutePath(final ConfigurationSection section, final String path) {
        final String currentPath = section.getCurrentPath();
        if (currentPath == null || currentPath.isEmpty()) {
            return path;
        }
        final Configuration root = section.getRoot();
        if (root == null) {
            throw new IllegalStateException("Cannot get options without a root");
        }
        return currentPath + root.options().pathSeparator() + path;
    }

    /**
     * Handles the {@link ConfigurationSection#set(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path of the value
     * @param value   The value to set
     */
    void set(ConfigurationSection section, String path, @Nullable Object value);

    /**
     * Handles the {@link ConfigurationSection#addDefault(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path of the value
     * @param value   The value to add
     */
    void addDefault(ConfigurationSection section, String path, @Nullable Object value);

    /**
     * Handles the {@link ConfigurationSection#createSection(String)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path to the section
     * @return The created section
     */
    ConfigurationSection createSection(ConfigurationSection section, String path);

    /**
     * Handles the {@link ConfigurationSection#set(String, Object)} method.
     *
     * @param section The {@link ConfigurationSection} from which the method was called
     * @param path    The path to the section
     * @param map     The values to set
     * @return The created section
     */
    ConfigurationSection createSection(ConfigurationSection section, String path, Map<?, ?> map);

    /**
     * Handles the {@link ConfigurationSection#setComments(String, List)} method.
     *
     * @param section  The {@link ConfigurationSection} from which the method was called
     * @param path     The path to the section
     * @param comments The comments to set
     */
    void setComments(ConfigurationSection section, String path, @Nullable List<String> comments);

    /**
     * Handles the {@link ConfigurationSection#setInlineComments(String, List)} method.
     *
     * @param section  The {@link ConfigurationSection} from which the method was called
     * @param path     The path to the section
     * @param comments The comments to set
     */
    void setInlineComments(ConfigurationSection section, String path, @Nullable List<String> comments);
}
