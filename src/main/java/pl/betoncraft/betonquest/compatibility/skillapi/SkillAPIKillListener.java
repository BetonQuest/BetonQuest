package pl.betoncraft.betonquest.compatibility.skillapi;

import com.sucy.skill.api.event.SkillDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.MobKillNotifier;

/**
 * Listens to kills by SkillAPI skills.
 */
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
