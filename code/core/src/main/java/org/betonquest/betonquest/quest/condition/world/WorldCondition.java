package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;
import org.bukkit.World;

/**
 * A condition that checks if the player is in a specific world.
 */
public class WorldCondition implements OnlineCondition {

    /**
     * The world to check.
     */
    private final Argument<World> world;

    /**
     * Create a new World condition.
     *
     * @param world the world to check
     */
    public WorldCondition(final Argument<World> world) {
        this.world = world;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final World world = this.world.getValue(profile);
        return profile.getPlayer().getWorld().equals(world);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
