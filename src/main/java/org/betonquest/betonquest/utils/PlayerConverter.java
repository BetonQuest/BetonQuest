package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Converts playerIDs to Player objects and back to playerIDs.
 */
@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.CommentRequired"})
public final class PlayerConverter {

    private PlayerConverter() {
    }

    /**
     * Returns playerID of the passed Player.
     *
     * @param player - Player object from which playerID needs to be extracted
     * @return playerID of the player
     */
    public static Profile getID(final OfflinePlayer player) {
        return new Profile() {
            @Override
            public OfflinePlayer getOfflinePlayer() {
                return player;
            }

            @Override
            public Player getPlayer() {
                return player.getPlayer();
            }

            @Override
            public Optional<Player> getOptionalPlayer() {
                return Optional.ofNullable(getPlayer());
            }

            @Override
            public boolean isOnline() {
                return false;
            }

            @Override
            public String getPlayerId() {
                return player.getUniqueId().toString();
            }

            @Override
            public String getPlayerName() {
                return player.getName();
            }
        };
    }

    /**
     * Returns playerID of the player with passed name.
     *
     * @param name - name of the player from which playerID needs to be extracted
     * @return playerID of the player
     */
    @SuppressWarnings("deprecation")
    public static Profile getID(final String name) {
        return getID(Bukkit.getOfflinePlayer(name));
    }
}
