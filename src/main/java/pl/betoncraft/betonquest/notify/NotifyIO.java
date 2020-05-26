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
package pl.betoncraft.betonquest.notify;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Used to display messages to a player
 * <p>
 * Data Valuues:
 * * sound: {sound_name} - What sound to play
 */
public abstract class NotifyIO {

    private final Map<String, String> data;

    public NotifyIO(Map<String, String> data) {
        this.data = data;
    }

    public NotifyIO() {
        this.data = new HashMap<>();
    }

    public Map<String, String> getData() {
        return data;
    }

    /**
     * Set a NotifyIO data option
     *
     * @param key   Data Key
     * @param value Data Value
     * @return ourself to allow chaining
     */
    public NotifyIO set(String key, String value) {
        data.put(key, value);
        return this;
    }

    public void sendToAll(String message) {
        sendNotify(message, Bukkit.getServer().getOnlinePlayers());
    }

    /**
     * Show to Specific Players
     *
     * @param players Players to show
     */

    public void sendNotify(String message, Player... players) {
        sendNotify(message, Arrays.asList(players));
    }

    /**
     * Show a notify to a collection of players
     */
    public void sendNotify(String message, Collection<? extends Player> players) {
        if (getData().containsKey("sound")) {
            for (Player player : players) {
                try {
                    player.playSound(player.getLocation(), Sound.valueOf(getData().get("sound")), 1F, 1F);
                } catch (IllegalArgumentException e) {
                    player.playSound(player.getLocation(), getData().get("sound"), 1F, 1F);
                    LogUtils.getLogger().log(Level.WARNING, "Could not play the right sound: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }
        }
    }
}
