package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Gives the player specified amount of experience
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceEvent extends QuestEvent {

    /**
     * The experience level the player needs to get.
     * The decimal part of the number is a percentage of the next level.
     */
    private final VariableNumber amount;
    private final boolean checkForLevel;

    public ExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.amount = instruction.getVarNum();
        this.checkForLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final double amount = this.amount.getDouble(profile);

        profile.getOnlineProfile()
                .map(OnlineProfile::getPlayer)
                .ifPresent(player -> {
                    if (checkForLevel) {
                        final double current = player.getLevel() + player.getExp();
                        final double amountToAdd = current + amount;
                        player.setLevel((int) amountToAdd);
                        player.setExp((float) (amountToAdd - (int) amountToAdd));
                    } else {
                        player.giveExp((int) amount);
                    }
                });
        return null;
    }
}
