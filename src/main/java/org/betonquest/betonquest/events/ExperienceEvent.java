package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * Gives the player specified amount of experience
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceEvent extends QuestEvent {

    private final VariableNumber amount;
    private final boolean checkForLevel;

    public ExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.amount = instruction.getVarNum();
        this.checkForLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().getOnlinePlayer();
        final int amount = this.amount.getInt(profile);
        if (checkForLevel) {
            player.giveExpLevels(amount);
        } else {
            player.giveExp(amount);
        }
        return null;
    }
}
