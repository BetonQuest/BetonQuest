package org.betonquest.betonquest.api.profiles;

import org.bukkit.entity.Player;


public interface OnlineProfile extends Profile {
    /**
     * @return the {@link Player} of the profile
     */
    Player getOnlinePlayer();
}
