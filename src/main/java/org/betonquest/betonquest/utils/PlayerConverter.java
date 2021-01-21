package org.betonquest.betonquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

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
    public static String getID(final Player player) {
        return player.getUniqueId().toString();
    }

    /**
     * Returns playerID of the player with passed name.
     *
     * @param name - name of the player from which playerID needs to be extracted
     * @return playerID of the player
     */
    @SuppressWarnings("deprecation")
    public static String getID(final String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
    }

    /**
     * Returns the Player object described by passed playerID.
     *
     * @param playerID - playerID
     * @return the Player object
     */
    public static Player getPlayer(final String playerID) {
        return Bukkit.getPlayer(UUID.fromString(playerID));
    }

    public static String getName(final String playerID) {
        return playerID == null ? null : Bukkit.getOfflinePlayer(UUID.fromString(playerID)).getName();
    }

}
