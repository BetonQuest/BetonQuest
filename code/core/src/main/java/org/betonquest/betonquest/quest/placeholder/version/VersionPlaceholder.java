package org.betonquest.betonquest.quest.placeholder.version;

import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.bukkit.plugin.Plugin;

/**
 * Displays version of the plugin.
 */
public class VersionPlaceholder implements PlayerlessPlaceholder {

    /**
     * The plugin to get the version of.
     */
    private final Plugin plugin;

    /**
     * Create a new version placeholder.
     *
     * @param plugin The plugin to get the version of.
     */
    public VersionPlaceholder(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getValue() {
        return plugin.getDescription().getVersion();
    }
}
