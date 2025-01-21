package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Notifies the MobKillObjective about the mob being killed. If your plugin
 * allows players to kill mobs without direct contact (like spells), call
 * addKill method each time the player kills a mob like that.
 */
public final class MobKillNotifier {
    /**
     * A list of all handlers for this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The static Notifier instance.
     */
    @Nullable
    private static MobKillNotifier instance;

    /**
     * Already counted entities.
     */
    private final List<UUID> entities = new ArrayList<>();

    private MobKillNotifier() {
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
     * @param killer the {@link Profile} that killed the mob
     * @param killed the mob that was killed
     */
    @SuppressWarnings("PMD.AvoidSynchronizedStatement")
    public static void addKill(final Profile killer, final Entity killed) {
        synchronized (MobKillNotifier.class) {
            if (instance == null) {
                instance = new MobKillNotifier();
            }
            if (instance.entities.contains(killed.getUniqueId())) {
                return;
            }
            instance.entities.add(killed.getUniqueId());
        }
        final MobKilledEvent event = new MobKilledEvent(killer, killed);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Is fired when BetonQuests receives info about a new, unique mob kill.
     */
    public static class MobKilledEvent extends ProfileEvent {

        /**
         * Killed entity.
         */
        private final Entity killed;

        /**
         * Create a new Mob killed event.
         *
         * @param killer the profile to progress
         * @param killed the entity to count as progress
         */
        public MobKilledEvent(final Profile killer, final Entity killed) {
            super(killer);
            this.killed = killed;
        }

        /**
         * Get the HandlerList of this event.
         *
         * @return the HandlerList.
         */
        public static HandlerList getHandlerList() {
            return HANDLER_LIST;
        }

        /**
         * Get the entity to progress.
         *
         * @return the entity that was killed
         */
        public Entity getEntity() {
            return killed;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLER_LIST;
        }
    }
}
