package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.MobKillNotifier;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import studio.magemonkey.fabled.api.event.SkillDamageEvent;

/**
 * Listens to kills by Fabled skills.
 */
public class FabledKillListener implements Listener {
    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The default constructor.
     *
     * @param profileProvider the profile provider instance
     */
    public FabledKillListener(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    /**
     * Listens to kills by Fabled skills.
     *
     * @param event the {@link SkillDamageEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(final SkillDamageEvent event) {
        if (!(event.getDamager() instanceof final Player player)) {
            return;
        }
        final LivingEntity target = event.getTarget();
        if (target.getHealth() > event.getDamage()) {
            return;
        }
        MobKillNotifier.addKill(profileProvider.getProfile(player), target);
    }
}
