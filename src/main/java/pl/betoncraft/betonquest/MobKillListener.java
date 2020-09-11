package pl.betoncraft.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.betoncraft.betonquest.api.MobKillNotifier;

/**
 * Listens to standard kills and adds them to MobKillNotifier.
 */
public class MobKillListener implements Listener {

    public MobKillListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer != null) {
            MobKillNotifier.addKill(killer, entity);
        }
    }
}
