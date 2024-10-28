package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import studio.magemonkey.fabled.api.event.SkillDamageEvent;

/**
 * Listens to kills by SkillAPI skills.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FabledKillListener implements Listener {

    public FabledKillListener() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(final SkillDamageEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (event.getTarget().getHealth() > event.getDamage()) {
            return;
        }
        MobKillNotifier.addKill(PlayerConverter.getID((Player) event.getDamager()), event.getTarget());
    }
}
