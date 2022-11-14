package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Sets the hunger level of the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HungerEvent extends QuestEvent {

    private final HungerAction action;
    private final short amount;

    public HungerEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);

        try {
            this.action = HungerAction.valueOf(instruction.next().toUpperCase(Locale.ROOT).trim());
            this.amount = Short.parseShort(instruction.next());
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Error while parsing hunger amount! Must be a number.", e);
        } catch (final IllegalArgumentException e) {
            throw new InstructionParseException("Error while parsing action! Must be 'set', 'give', or 'take'.", e);
        }

        if (amount < 0 || amount > 20) {
            throw new InstructionParseException("Hunger amount must be between 0 and 20! Event will be ignored.");
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final Profile profile) {
        final Player player = profile.getOnlineProfile().get().getPlayer();

        switch (this.action) {
            case GIVE -> player.setFoodLevel(Math.min(player.getFoodLevel() + this.amount, 20));
            case TAKE -> player.setFoodLevel(Math.max(player.getFoodLevel() - this.amount, 0));
            case SET -> player.setFoodLevel(this.amount);
        }
        return null;
    }

    private enum HungerAction {
        GIVE,
        TAKE,
        SET
    }


}
