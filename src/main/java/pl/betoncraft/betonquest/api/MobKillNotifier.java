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
package pl.betoncraft.betonquest.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;

import java.util.ArrayList;

/**
 * Notifies the MobKillObjective about the mob being killed. If your plugin
 * allows players to kill mobs without direct contact (like spells), call
 * addKill method each time the player kills a mob like that.
 *
 * @author Jakub Sapalski
 */
public class MobKillNotifier {

    private static final HandlerList handlers = new HandlerList();

    private static MobKillNotifier instance;
    private BukkitRunnable cleaner;
    private ArrayList<Entity> entities = new ArrayList<>();

    public MobKillNotifier() {
        instance = this;
        cleaner = new BukkitRunnable() {
            @Override
            public void run() {
                entities.clear();
            }
        };
        cleaner.runTaskTimer(BetonQuest.getInstance(), 1, 1);
    }

    /**
     * Call this method when you detect that a player killed a mob in a
     * non-standard way (i.e. a spell, projectile weapon etc.)
     *
     * @param killer the player that killed the mob
     * @param killed the mob that was killed
     */
    public static void addKill(Player killer, Entity killed) {
        if (instance == null)
            new MobKillNotifier();
        if (instance.entities.contains(killed))
            return;
        instance.entities.add(killed);
        MobKilledEvent event = new MobKilledEvent(killer, killed);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Is fired when BetonQuests receives info about a new, unique mob kill.
     */
    public static class MobKilledEvent extends Event {

        private Player killer;
        private Entity killed;

        public MobKilledEvent(Player killer, Entity killed) {
            this.killer = killer;
            this.killed = killed;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }

        /**
         * @return the player that killed this entity
         */
        public Player getPlayer() {
            return killer;
        }

        /**
         * @return the entity that was killed
         */
        public Entity getEntity() {
            return killed;
        }

        public HandlerList getHandlers() {
            return handlers;
        }

    }

}
