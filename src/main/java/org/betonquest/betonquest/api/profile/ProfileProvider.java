package org.betonquest.betonquest.api.profile;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Interface for implementing a profile provider.
 * Profile providers are responsible for creating {@link Profile}s and
 * {@link OnlineProfile}s for players.
 *
 * A default implementation is available at {@link UUIDProfileProvider}.
 */
public interface ProfileProvider {
    /**
     * Get a {@link Profile} for an offline player.
     *
     * @param offlinePlayer the offline player to get the profile for
     * @return the profile
     */
    Profile getProfile(OfflinePlayer offlinePlayer);

    /**
     * Get a {@link Profile} for a player uuid.
     *
     * @param player the player to get the profile for
     * @return the profile
     */
    Profile getProfile(UUID uuid);

    /**
     * Get an {@link OnlineProfile} for a player.
     *
     * @param player the player to get the profile for
     * @return the online profile
     */
    OnlineProfile getProfile(Player player);

    /**
     * Get all online profiles.
     *
     * @return all online profiles
     */
    List<OnlineProfile> getOnlineProfiles();
}
