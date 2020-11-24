package pl.betoncraft.betonquest.variables;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * This variable resolves into the player's name. It can has optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNameVariable extends Variable {

    private final boolean display;

    public PlayerNameVariable(final Instruction instruction) {
        super(instruction);
        display = instruction.hasArgument("display");
    }

    @Override
    public String getValue(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        return display ? player.getDisplayName() : player.getName();
    }

}
