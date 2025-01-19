package org.betonquest.betonquest.util;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Converts the player to the Profile
 */
@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.CommentRequired"})
public final class PlayerConverter {

    private PlayerConverter() {
    }

    /**
     * Returns the {@link Profile} of the passed {@link OfflinePlayer}.
     *
     * @param player - Player object to get the Profile from
     * @return profile of the player
     */
    public static Profile getID(final OfflinePlayer player) {
        return new Profile() {
            @Override
            public OfflinePlayer getPlayer() {
                return player;
            }

            @Override
            public UUID getProfileUUID() {
                return player.getUniqueId();
            }

            @Override
            public String getProfileName() {
                return player.getName();
            }

            @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
            @Override
            public Optional<OnlineProfile> getOnlineProfile() {
                final Player onlinePlayer = player.getPlayer();
                if (onlinePlayer == null) {
                    return Optional.empty();
                }
                return Optional.of(getID(onlinePlayer));
            }

            @Override
            public String toString() {
                return player.getName() + " with profile " + this.getProfileName();
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getProfileUUID());
            }
        };
    }

    /**
     * Returns the {@link OnlineProfile} of the passed {@link Player}.
     *
     * @param player - Player object to get the Profile from
     * @return profile of the player
     */
    public static OnlineProfile getID(final Player player) {
        return new OnlineProfile() {
            @Override
            public Player getPlayer() {
                return player;
            }

            @Override
            public UUID getProfileUUID() {
                return player.getUniqueId();
            }

            @Override
            public String getProfileName() {
                return player.getName();
            }

            @Override
            public Optional<OnlineProfile> getOnlineProfile() {
                if (player.isOnline()) {
                    return Optional.of(this);
                }
                return Optional.empty();
            }

            @Override
            public String toString() {
                return player.getName() + " with profile " + this.getProfileName();
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getProfileUUID());
            }
        };
    }

    /**
     * Get all online {@link OnlineProfile}s.
     *
     * @return A list of {@link OnlineProfile}s
     */
    public static List<OnlineProfile> getOnlineProfiles() {
        return Bukkit.getOnlinePlayers().stream().map(PlayerConverter::getID).toList();
    }
}
