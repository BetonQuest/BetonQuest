package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.util.PlayerConverter;
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
        MobKillNotifier.addKill(PlayerConverter.getID(event.getAttacker().getPlayer()), event.getDefender().getEntity());
    }
}
