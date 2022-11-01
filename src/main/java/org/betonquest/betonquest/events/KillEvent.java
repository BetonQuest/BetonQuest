package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

/**
 * Simply kills the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class KillEvent extends QuestEvent {

    public KillEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
    }

    @Override
    protected Void execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().getOnlinePlayer();
        player.damage(player.getHealth() + 1);
        return null;
    }

}
