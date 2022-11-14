package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.entity.Player;

/**
 * This variable resolves into the player's name. It can has optional "display"
 * argument, which will resolve it to the display name.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerNameVariable extends Variable {

    private final boolean display;

    public PlayerNameVariable(final Instruction instruction) {
        super(instruction);
        display = instruction.hasArgument("display");
    }

    @Override
    public String getValue(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        return display ? player.getDisplayName() : player.getName();
    }

}
