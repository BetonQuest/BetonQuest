package org.betonquest.betonquest.api.common.worldselector;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.World;

/**
 * A selector to get the appropriate world for a given profile.
 */
public interface WorldSelector {
    /**
     * Get the world that should be used for the given profile.
     *
     * @param profile profile to get the world for
     * @return the world to use
     * @throws QuestRuntimeException if the world to use cannot be determined
     */
    World getWorld(Profile profile) throws QuestRuntimeException;
}
