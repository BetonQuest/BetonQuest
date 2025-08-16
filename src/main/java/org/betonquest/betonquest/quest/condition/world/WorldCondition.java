package org.betonquest.betonquest.quest.condition.world;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.World;

/**
 * A condition that checks if the player is in a specific world.
 */
public class WorldCondition implements OnlineCondition {

    /**
     * The world to check.
     */
    private final Variable<World> variableWorld;

    /**
     * Create a new World condition.
     *
     * @param world the world to check
     */
    public WorldCondition(final Variable<World> world) {
        this.variableWorld = world;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final World world = variableWorld.getValue(profile);
        return profile.getPlayer().getWorld().equals(world);
    }
}
