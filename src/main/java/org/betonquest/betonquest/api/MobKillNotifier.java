package org.betonquest.betonquest.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Notifies the MobKillObjective about the mob being killed. If your plugin
 * allows players to kill mobs without direct contact (like spells), call
 * addKill method each time the player kills a mob like that.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MobKillNotifier {

    private static final HandlerList HANDLERS = new HandlerList();

    private static MobKillNotifier instance;
    private final List<Entity> entities = new ArrayList<>();

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public MobKillNotifier() {
        instance = this;
        final BukkitRunnable cleaner = new BukkitRunnable() {
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
    public static void addKill(final Player killer, final Entity killed) {
        if (instance == null) {
            new MobKillNotifier();
        }
        if (instance.entities.contains(killed)) {
            return;
        }
        instance.entities.add(killed);
        final MobKilledEvent event = new MobKilledEvent(killer, killed);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Is fired when BetonQuests receives info about a new, unique mob kill.
     */
    public static class MobKilledEvent extends Event {

        private final Player killer;
        private final Entity killed;

        public MobKilledEvent(final Player killer, final Entity killed) {
            super();
            this.killer = killer;
            this.killed = killed;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
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

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }

    }

}
