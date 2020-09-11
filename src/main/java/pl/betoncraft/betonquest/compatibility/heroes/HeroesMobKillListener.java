package pl.betoncraft.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.MobKillNotifier;

/**
 * Handles kills done by Heroes plugin and passes them to MobKillNotifier.
 */
public class HeroesMobKillListener implements Listener {

    public HeroesMobKillListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeroesKill(final HeroKillCharacterEvent event) {
        MobKillNotifier.addKill(event.getAttacker().getPlayer(), event.getDefender().getEntity());
    }

}
