package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.MobKillNotifier;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles kills done by Heroes plugin and passes them to MobKillNotifier.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesMobKillListener implements Listener {

    public HeroesMobKillListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeroesKill(final HeroKillCharacterEvent event) {
        MobKillNotifier.addKill(event.getAttacker().getPlayer(), event.getDefender().getEntity());
    }

}
