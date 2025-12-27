package org.betonquest.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * A BetonQuest placeholder which delegates to PAPI.
 */
public class PlaceholderAPIPlaceholder implements NullablePlaceholder {

    /**
     * Placeholder to resolve without surrounding '%'.
     */
    private final String placeholder;

    /**
     * Create a new Placeholder API placeholder.
     *
     * @param placeholder the placeholder to set
     */
    public PlaceholderAPIPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        final OfflinePlayer player = profile == null ? null : profile.getPlayer();
        return PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
    }
}
