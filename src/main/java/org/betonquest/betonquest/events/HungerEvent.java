package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Sets the hunger level of the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HungerEvent extends QuestEvent {

    private final HungerAction action;
    private final short amount;

    public HungerEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        this.action = HungerAction.get(instruction.next());
        try {
            this.amount = Short.parseShort(instruction.next());
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Error while parsing hunger amount! Must be a number.", e);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);

        switch (this.action) {
            case GIVE -> player.setFoodLevel(Math.min(player.getFoodLevel() + this.amount, 20));
            case TAKE -> player.setFoodLevel(Math.max(player.getFoodLevel() - this.amount, 0));
            case SET -> player.setFoodLevel(this.amount);
        }
        return null;
    }

    private enum HungerAction {
        GIVE("give", "add"),
        TAKE("take", "remove"),
        SET("set");

        private final String[] aliases;

        HungerAction(final String... aliases) {
            this.aliases = aliases.clone();
        }

        public static HungerAction get(final String alias) throws InstructionParseException {
            for (final HungerAction action : values()) {
                if (Arrays.stream(action.aliases).anyMatch(a -> a.equalsIgnoreCase(alias))) {
                    return action;
                }
            }
            throw new InstructionParseException("Cannot parse hunger action '" + alias + "'");
        }
    }


}
