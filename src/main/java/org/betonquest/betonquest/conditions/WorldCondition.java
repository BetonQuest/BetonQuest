package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;

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
