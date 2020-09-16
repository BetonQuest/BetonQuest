package pl.betoncraft.betonquest.conditions;

import org.bukkit.GameMode;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Locale;

public class GameModeCondition extends Condition {

    private GameMode gameMode;

    public GameModeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next().toUpperCase(Locale.ROOT);
        try {
            gameMode = GameMode.valueOf(string);
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("No such gamemode: " + string, e);
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        return PlayerConverter.getPlayer(playerID).getGameMode() == gameMode;
    }

}
