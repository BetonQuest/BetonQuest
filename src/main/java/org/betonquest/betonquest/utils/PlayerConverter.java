package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
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
            public OfflinePlayer getOfflinePlayer() {
                return player;
            }

            @Override
            public Optional<Player> getPlayer() {
                return Optional.ofNullable(player.getPlayer());
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
            public Boolean isPlayerOnline() {
                return player.isOnline();
            }

            @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
            @Override
            public OnlineProfile getOnlineProfile() throws IllegalStateException {
                final Player onlinePlayer = player.getPlayer();
                if (onlinePlayer == null) {
                    throw new IllegalStateException("Player is Offline!");
                }
                return getID(onlinePlayer);
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(player.getUniqueId());
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
            public Player getOnlinePlayer() {
                return player;
            }

            @Override
            public OfflinePlayer getOfflinePlayer() {
                return player;
            }

            @Override
            public Optional<Player> getPlayer() {
                return Optional.of(player);
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
            public OnlineProfile getOnlineProfile() {
                return this;
            }

            @Override
            public Boolean isPlayerOnline() {
                return player.isOnline();
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(player.getUniqueId());
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
