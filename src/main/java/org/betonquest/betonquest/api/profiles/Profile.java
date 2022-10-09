package org.betonquest.betonquest.api.profiles;

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
    OfflinePlayer getOfflinePlayer();

    /**
     * @return the {@link Player} that this profile belongs to, wrapped into an {@link Optional} since the player
     * may be offline
     */
    Optional<Player> getPlayer();

    /**
     * @return the {@link UUID} of the profile
     */
    UUID getProfileUUID();

    /**
     * @return the name of the profile
     */
    String getProfileName();

    /**
     * @return true if the {@link Player} is online
     */
    Boolean isPlayerOnline();

    /**
     * Gets the {@link OnlineProfile} of this Profile if the {@link Player} is online.
     *
     * @return The {@link OnlineProfile} of this Profile
     * @throws IllegalStateException is thrown, if the {@link Player} is offline
     */
    @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
    OnlineProfile getOnlineProfile() throws IllegalStateException;
}
