package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Spawns an explosion in a given location and with given flags
 */
public class ExplosionEvent extends QuestEvent {

    private final boolean setsFire;
    private final boolean breaksBlocks;
    private final VariableNumber power;
    private final LocationData loc;

    public ExplosionEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        setsFire = instruction.next().equals("1");
        breaksBlocks = instruction.next().equals("1");
        power = instruction.getVarNum();
        loc = instruction.getLocation();

    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(),
                (float) power.getDouble(playerID), setsFire, breaksBlocks);
        return null;
    }
}
