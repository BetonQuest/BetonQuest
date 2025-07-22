package org.betonquest.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariable;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * A BetonQuest variable which delegates to PAPI.
 */
public class PlaceholderVariable implements NullableVariable {
    /**
     * Placeholder to resolve without surrounding '%'.
     */
    private final String placeholder;

    /**
     * Create a new Placeholder API variable.
     *
     * @param placeholder the placeholder to set
     */
    public PlaceholderVariable(final String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final OfflinePlayer player = profile == null ? null : profile.getPlayer();
        return PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
    }
}
