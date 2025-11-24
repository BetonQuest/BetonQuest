package org.betonquest.betonquest.listener;

import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Listens to standard kills and adds them to MobKillNotifier.
 */
public class MobKillListener implements Listener {

    /**
     * Profile provider.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create a new standard Mob Kill Listener.
     *
     * @param profileProvider the profile provider
     */
    public MobKillListener(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    /**
     * Adds the death to the notifier if it was killed by a player.
     *
     * @param event the entity death event
     */
    @EventHandler(ignoreCancelled = true)
    public void onKill(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer != null) {
            MobKillNotifier.addKill(profileProvider.getProfile(killer), entity);
        }
    }
}
