/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Converts playerIDs to Player objects and back to playerIDs.
 *
 * @author Jakub Sapalski
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class PlayerConverter {

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
