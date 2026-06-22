package org.betonquest.betonquest.api.profile;

import org.bukkit.entity.Player;

/**
 * The OnlineProfile extends the {@link Profile} with the assumption that the profile's player is online.
 *
 * @since 3.0.0
 */
public interface OnlineProfile extends Profile {

    /**
     * Gets the player this profile belongs to.
     *
     * @return the {@link Player} of the profile
     * @since 3.0.0
     */
    @Override
    Player getPlayer();
}
