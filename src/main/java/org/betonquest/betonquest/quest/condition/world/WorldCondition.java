package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.World;

/**
 * A condition that checks if the player is in a specific world.
 */
public class WorldCondition implements PlayerCondition {

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
    public boolean check(final Profile profile) throws QuestRuntimeException {
        return profile.getOnlineProfile().get().getPlayer().getWorld().equals(world);
    }
}
