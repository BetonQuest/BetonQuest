package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Checks if the player is in the specified world.
 */
@SuppressWarnings("PMD.CommentRequired")
public class WorldCondition extends Condition {

    private final World world;

    public WorldCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        world = getWorld(instruction.next());
    }

    private World getWorld(final String name) throws InstructionParseException {
        final World world = Bukkit.getWorld(name);
        if (world != null) {
            return world;
        }
        try {
            return new CompoundLocation(instruction.getPackage(), name).getLocation(null).getWorld();
        } catch (InstructionParseException | QuestRuntimeException e) {
            throw new InstructionParseException("There is no such world: " + name, e);
        }
    }

    @Override
    protected Boolean execute(final Profile profile) {
        return profile.getOnlineProfile().get().getPlayer().getWorld().equals(world);
    }

}
