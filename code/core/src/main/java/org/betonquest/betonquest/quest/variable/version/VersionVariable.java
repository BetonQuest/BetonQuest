package org.betonquest.betonquest.quest.variable.version;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.bukkit.plugin.Plugin;

/**
 * Displays version of the plugin.
 */
public class VersionVariable implements PlayerlessVariable {

    /**
     * The plugin to get the version of.
     */
    private final Plugin plugin;

    /**
     * Create a new version variable.
     *
     * @param plugin The plugin to get the version of.
     */
    public VersionVariable(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getValue() throws QuestException {
        return plugin.getDescription().getVersion();
    }
}
