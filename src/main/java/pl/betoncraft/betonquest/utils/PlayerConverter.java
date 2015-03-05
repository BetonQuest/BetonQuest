/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * Converts playerIDs to Player objects and back to playerIDs.
 * 
 * @author Coosh
 */
@SuppressWarnings("deprecation")
public class PlayerConverter {

    private static PlayerConversionType type;

    static {
        String uuid = BetonQuest.getInstance().getConfig().getString("uuid");
        if (uuid != null && uuid.equals("true")) {
            type = PlayerConversionType.UUID;
            BetonQuest.getInstance().getLogger().info("Using UUID!");
        } else {
            type = PlayerConversionType.NAME;
            BetonQuest.getInstance().getLogger().info("Using Names!");
        }
    }

    public enum PlayerConversionType {
        UUID, NAME
    }

    /**
     * Returns playerID of the passed Player.
     * 
     * @param player
     *            - Player object from which playerID needs to be extracted
     * @return playerID of the player
     */
    public static String getID(Player player) {
        if (type == PlayerConversionType.NAME) {
            return player.getName();
        } else if (type == PlayerConversionType.UUID) {
            return player.getUniqueId().toString();
        } else {
            return null;
        }
    }

    /**
     * Retruns playerID of the player with passed name.
     * 
     * @param name
     *            - name of the player from which playerID needs to be extracted
     * @return playerID of the player
     */
    public static String getID(String name) {
        if (type == PlayerConversionType.NAME) {
            return name;
        } else if (type == PlayerConversionType.UUID) {
            return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
        } else {
            return null;
        }
    }

    /**
     * Returns the Player object described by passed playerID.
     * 
     * @param ID
     *            - playerID
     * @return the Player object
     */
    public static Player getPlayer(String ID) {
        if (type == PlayerConversionType.NAME) {
            return Bukkit.getPlayer(ID);
        } else if (type == PlayerConversionType.UUID) {
            return Bukkit.getPlayer(UUID.fromString(ID));
        } else {
            return null;
        }
    }
    
    public static String getName(String playerID) {
        if (type == PlayerConversionType.NAME) {
            return playerID;
        } else if (type == PlayerConversionType.UUID) {
            return Bukkit.getOfflinePlayer(UUID.fromString(playerID)).getName();
        } else {
            return null;
        }
    }

    /**
     * Returns which conversion type is being used.
     * 
     * @return PlayerConversionType type of the conversion
     */
    public static PlayerConversionType getType() {
        return type;
    }

}
