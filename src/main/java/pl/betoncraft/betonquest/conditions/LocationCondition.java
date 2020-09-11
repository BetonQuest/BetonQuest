package pl.betoncraft.betonquest.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to be in specified distance from a location
 */
public class LocationCondition extends Condition {

    private final LocationData loc;
    private final VariableNumber range;

    public LocationCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation();
        range = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        final Player player = PlayerConverter.getPlayer(playerID);
        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }
        final double pRange = range.getDouble(playerID);
        return player.getLocation().distanceSquared(location) <= pRange * pRange;
    }

}
