package org.betonquest.betonquest.api.profile;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * The profile allows a single {@link Player} to have multiple independent quest states.
 */
public interface Profile {

    /**
     * @return the {@link OfflinePlayer} that this profile belongs to
     */
    OfflinePlayer getPlayer();

    /**
     * @return the {@link UUID} of the profile
     */
    UUID getProfileUUID();

    /**
     * @return the {@link UUID} of the player
     */
    default UUID getPlayerUUID() {
        return getPlayer().getUniqueId();
    }

    /**
     * @return the name of the profile
     */
    String getProfileName();

    /**
     * Gets the {@link OnlineProfile} of this Profile if the {@link Player} is online.
     *
     * @return The {@link OnlineProfile} of this Profile
     */
    Optional<OnlineProfile> getOnlineProfile();

    /**
     * @return the name of the player with the name of the profile
     */
    @Override
    String toString();
}
