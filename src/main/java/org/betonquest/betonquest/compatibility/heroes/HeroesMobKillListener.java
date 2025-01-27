package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.MobKillNotifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles kills done by Heroes plugin and passes them to MobKillNotifier.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeroesMobKillListener implements Listener {

    public HeroesMobKillListener() {
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeroesKill(final HeroKillCharacterEvent event) {
        MobKillNotifier.addKill(BetonQuest.getInstance().getProfileProvider().getProfile(event.getAttacker().getPlayer()), event.getDefender().getEntity());
    }
}
