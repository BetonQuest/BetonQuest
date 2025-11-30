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
     * Gets the offline player this profile belongs to.
     *
     * @return the {@link OfflinePlayer} that this profile belongs to
     */
    OfflinePlayer getPlayer();

    /**
     * Gets the unique id of this profile.
     *
     * @return the {@link UUID} of the profile
     */
    UUID getProfileUUID();

    /**
     * Gets the unique id of the player this profile belongs to.
     *
     * @return the {@link UUID} of the player
     */
    default UUID getPlayerUUID() {
        return getPlayer().getUniqueId();
    }

    /**
     * Gets the profile name.
     *
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
     * Returns the string representation of the profile.
     *
     * @return the name of the player with the name of the profile
     */
    @Override
    String toString();
}
