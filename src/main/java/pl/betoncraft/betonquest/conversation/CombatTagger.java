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
package pl.betoncraft.betonquest.conversation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tags players that are in combat to prevent them from starting the
 * conversation
 *
 * @author Jakub Sapalski
 */
public class CombatTagger implements Listener {

    private static HashMap<String, Boolean> tagged = new HashMap<>();
    private static HashMap<String, BukkitRunnable> untaggers = new HashMap<>();
    private int delay = 10;

    /**
     * Starts the combat listener
     */
    public CombatTagger() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        delay = Integer.parseInt(Config.getString("config.combat_delay"));
    }

    /**
     * Checks if the player is combat-tagged
     *
     * @param playerID ID of the player
     * @return true if the player is tagged, false otherwise
     */
    public static boolean isTagged(String playerID) {
        boolean result = false;
        Boolean state = tagged.get(playerID);
        if (state != null) {
            result = state;
        }
        return result;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        ArrayList<String> IDs = new ArrayList<>();
        if (event.getEntity() instanceof Player) {
            IDs.add(PlayerConverter.getID((Player) event.getEntity()));
        }
        if (event.getDamager() instanceof Player) {
            IDs.add(PlayerConverter.getID((Player) event.getDamager()));
        }
        for (final String playerID : IDs) {
            tagged.put(playerID, true);
            BukkitRunnable run = untaggers.get(playerID);
            if (run != null) {
                run.cancel();
            }
            untaggers.put(playerID, new BukkitRunnable() {
                @Override
                public void run() {
                    tagged.put(playerID, false);
                }
            });
            untaggers.get(playerID).runTaskLater(BetonQuest.getInstance(), delay * 20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        String playerID = PlayerConverter.getID(event.getEntity());
        tagged.remove(playerID);
        BukkitRunnable runnable = untaggers.remove(playerID);
        if (runnable != null)
            runnable.cancel();
    }
}
