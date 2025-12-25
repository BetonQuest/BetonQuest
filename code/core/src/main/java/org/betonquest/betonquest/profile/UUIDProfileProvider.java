package org.betonquest.betonquest.profile;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation for {@link ProfileProvider}.
 */
public class UUIDProfileProvider implements ProfileProvider {

    /**
     * The server instance used to get player from UUID.
     */
    private final Server server;

    /**
     * Default profile provider constructor.
     *
     * @param server the server instance used to get player from UUID
     */
    public UUIDProfileProvider(final Server server) {
        this.server = server;
    }

    @Override
    public Profile getProfile(final OfflinePlayer player) {
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

            @Override
            public Optional<OnlineProfile> getOnlineProfile() {
                final Player onlinePlayer = player.getPlayer();
                if (onlinePlayer == null) {
                    return Optional.empty();
                }
                return Optional.of(getProfile(onlinePlayer));
            }

            @Override
            public String toString() {
                return player.getName() + " with profile " + this.getProfileName();
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof final Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getProfileUUID());
            }
        };
    }

    @Override
    public OnlineProfile getProfile(final Player player) {
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
                return obj instanceof final Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(getProfileUUID());
            }
        };
    }

    @Override
    public List<OnlineProfile> getOnlineProfiles() {
        return server.getOnlinePlayers().stream().map(this::getProfile).toList();
    }

    @Override
    public Profile getProfile(final UUID uuid) {
        final OfflinePlayer player = server.getOfflinePlayer(uuid);
        return getProfile(player);
    }
}
