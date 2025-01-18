package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Notifies the MobKillObjective about the mob being killed. If your plugin
 * allows players to kill mobs without direct contact (like spells), call
 * addKill method each time the player kills a mob like that.
 */
@SuppressWarnings("PMD.CommentRequired")
public final class MobKillNotifier {

    private static final HandlerList HANDLERS = new HandlerList();

    @Nullable
    private static MobKillNotifier instance;

    private final List<Entity> entities = new ArrayList<>();

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
            if (instance.entities.contains(killed)) {
                return;
            }
            instance.entities.add(killed);
        }
        final MobKilledEvent event = new MobKilledEvent(killer, killed);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Is fired when BetonQuests receives info about a new, unique mob kill.
     */
    public static class MobKilledEvent extends ProfileEvent {

        private final Entity killed;

        public MobKilledEvent(final Profile killer, final Entity killed) {
            super(killer);
            this.killed = killed;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
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
