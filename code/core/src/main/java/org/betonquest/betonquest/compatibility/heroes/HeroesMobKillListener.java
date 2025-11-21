package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.api.events.HeroKillCharacterEvent;
import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles kills done by Heroes plugin and passes them to MobKillNotifier.
 */
public class HeroesMobKillListener implements Listener {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new Kill Listener for Heroes.
     *
     * @param profileProvider the profile provider instance
     */
    public HeroesMobKillListener(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    /**
     * Adds a kill to the MobKillNotifier.
     *
     * @param event The {@link HeroKillCharacterEvent} event of Heroes.
     */
    @EventHandler(ignoreCancelled = true)
    public void onHeroesKill(final HeroKillCharacterEvent event) {
        MobKillNotifier.addKill(profileProvider.getProfile(event.getAttacker().getPlayer()), event.getDefender().getEntity());
    }
}
