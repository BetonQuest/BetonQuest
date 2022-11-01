package org.betonquest.betonquest.api.profiles;

import org.bukkit.entity.Player;

/**
 * The OnlineProfile extends the {@link Profile} with the assumption that the profile's player is online.
 */
public interface OnlineProfile extends Profile {
    /**
     * @return the {@link Player} of the profile
     */
    Player getOnlinePlayer();
}
