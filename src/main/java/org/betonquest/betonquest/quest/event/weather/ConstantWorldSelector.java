package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.World;

/**
 * World selector that always selects the specified world.
 */
public class ConstantWorldSelector implements WorldSelector {
    /**
     * The world that should always be used.
     */
    private final World world;

    /**
     * Create a selector that will always select the provided world.
     *
     * @param world world to be selected
     */
    public ConstantWorldSelector(final World world) {
        this.world = world;
    }

    @Override
    public World getWorld(final Profile profile) {
        return world;
    }
}
