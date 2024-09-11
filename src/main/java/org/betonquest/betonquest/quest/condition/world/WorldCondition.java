package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.World;

/**
 * A condition that checks if the player is in a specific world.
 */
public class WorldCondition implements OnlineCondition {

    /**
     * The world to check.
     */
    private final World world;

    /**
     * Create a new World condition.
     *
     * @param world the world to check
     */
    public WorldCondition(final World world) {
        this.world = world;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return profile.getPlayer().getWorld().equals(world);
    }
}
