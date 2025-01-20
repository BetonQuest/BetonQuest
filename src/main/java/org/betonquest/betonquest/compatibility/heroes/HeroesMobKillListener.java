package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.MobKillNotifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles kills done by Heroes plugin and passes them to MobKillNotifier.
 */
public class HeroesMobKillListener implements Listener {

    /**
     * The default constructor.
     */
    public HeroesMobKillListener() {
        // Empty
    }

    /**
     * Adds a kill to the MobKillNotifier.
     *
     * @param event The {@link HeroKillCharacterEvent} event of Heroes.
     */
    @EventHandler(ignoreCancelled = true)
    public void onHeroesKill(final HeroKillCharacterEvent event) {
        MobKillNotifier.addKill(BetonQuest.getInstance().getProfileProvider().getProfile(event.getAttacker().getPlayer()), event.getDefender().getEntity());
    }
}
