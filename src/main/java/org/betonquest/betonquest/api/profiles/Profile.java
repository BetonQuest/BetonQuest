package org.betonquest.betonquest.api.profiles;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * The profile extends the {@link Player} to be able to have multiple quest-progresses
 */
public interface Profile {

    /**
     * @return the {@link OfflinePlayer} from the profile
     */
    OfflinePlayer getOfflinePlayer();

    /**
     * @return the {@link Player} of the profile, wrapped into an {@link Optional}
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
}
