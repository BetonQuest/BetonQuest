package org.betonquest.betonquest.compatibility.skillapi;

import com.sucy.skill.api.event.SkillDamageEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.MobKillNotifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listens to kills by SkillAPI skills.
 */
@SuppressWarnings("PMD.CommentRequired")
public class SkillAPIKillListener implements Listener {

    public SkillAPIKillListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(final SkillDamageEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (event.getTarget().getHealth() > event.getDamage()) {
            return;
        }
        final Player player = (Player) event.getDamager();
        MobKillNotifier.addKill(player, event.getTarget());
    }

}
