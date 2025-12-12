package org.betonquest.betonquest.api.profile;

import org.bukkit.entity.Player;

/**
 * The OnlineProfile extends the {@link Profile} with the assumption that the profile's player is online.
 */
public interface OnlineProfile extends Profile {
    /**
     * Gets the player this profile belongs to.
     *
     * @return the {@link Player} of the profile
     */
    @Override
    Player getPlayer();
}
