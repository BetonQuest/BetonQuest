package org.betonquest.betonquest;

import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Listens to standard kills and adds them to MobKillNotifier.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MobKillListener implements Listener {

    public MobKillListener() {
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer != null) {
            MobKillNotifier.addKill(PlayerConverter.getID(killer), entity);
        }
    }
}
