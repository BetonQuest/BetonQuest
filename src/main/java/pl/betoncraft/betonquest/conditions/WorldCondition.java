package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

/**
 * Checks if the player is in the specified world.
 */
@SuppressWarnings("PMD.CommentRequired")
public class WorldCondition extends Condition {

    private World world;

    public WorldCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String name = instruction.next();
        world = Bukkit.getWorld(name);
        if (world == null) {
            try {
                world = new CompoundLocation(instruction.getPackage().getName(), name).getLocation(null).getWorld();
            } catch (InstructionParseException | QuestRuntimeException e) {
                throw new InstructionParseException("There is no such world: " + name, e);
            }
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        return PlayerConverter.getPlayer(playerID).getWorld().equals(world);
    }

}
