package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.GameMode;

import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class GameModeCondition extends Condition {

    private final GameMode gameMode;

    public GameModeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next().toUpperCase(Locale.ROOT);
        try {
            gameMode = GameMode.valueOf(string);
        } catch (final IllegalArgumentException e) {
            throw new InstructionParseException("No such gamemode: " + string, e);
        }
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return profile.getOnlineProfile().get().getPlayer().getGameMode() == gameMode;
    }

}
