package org.betonquest.betonquest.quest.placeholder.version;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Displays version of the plugin.
 */
public class VersionPlaceholder implements NullablePlaceholder {

    /**
     * The plugin to get the version of.
     */
    private final Argument<Plugin> plugin;

    /**
     * Create a new version placeholder.
     *
     * @param plugin The plugin to get the version of.
     */
    public VersionPlaceholder(final Argument<Plugin> plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return plugin.getValue(profile).getDescription().getVersion();
    }
}
