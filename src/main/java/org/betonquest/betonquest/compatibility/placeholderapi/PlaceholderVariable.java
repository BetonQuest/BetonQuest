package org.betonquest.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * A BetonQuest variable which delegates to PAPI.
 */
public class PlaceholderVariable extends Variable {
    /**
     * Placeholder to resolve without surrounding '%'.
     */
    private final String placeholder;

    /**
     * Create a new Placeholder API variable.
     *
     * @param instruction the instruction to parse
     */
    public PlaceholderVariable(final Instruction instruction) {
        super(instruction);
        staticness = true;
        placeholder = String.join(".", instruction.getValueParts());
    }

    @Override
    public String getValue(@Nullable final Profile profile) {
        final OfflinePlayer player = profile == null ? null : profile.getPlayer();
        return PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
    }
}
