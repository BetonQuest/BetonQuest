package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tags profiles that are in combat to prevent them from starting the conversation.
 */
public class CombatTagger implements Listener {
    /**
     * Contains a player profile if it is tagged as "in combat".
     * <p>
     * The Runnable removes the entry from the same map after the delay.
     */
    private static final Map<Profile, BukkitRunnable> TAGGERS = new HashMap<>();

    /**
     * Delay in seconds after a player profile is untagged from "in combat".
     */
    private final int delay;

    /**
     * Create the combat listener.
     *
     * @param delay the delay in seconds after a player profile is untagged from "in combat"
     */
    public CombatTagger(final int delay) {
        this.delay = delay;
    }

    /**
     * Checks if the profile is combat-tagged.
     *
     * @param profile the {@link Profile} to check
     * @return true if the profile is tagged, false otherwise
     */
    public static boolean isTagged(final Profile profile) {
        return TAGGERS.containsKey(profile);
    }

    /**
     * Tags a player as "in combat" if dealing or taking damage to or from an entity.
     *
     * @param event the event to listen
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final List<Profile> profiles = new ArrayList<>();
        if (event.getEntity() instanceof Player) {
            profiles.add(PlayerConverter.getID((Player) event.getEntity()));
        }
        if (event.getDamager() instanceof Player) {
            profiles.add(PlayerConverter.getID((Player) event.getDamager()));
        }
        for (final Profile profile : profiles) {
            final BukkitRunnable run = TAGGERS.get(profile);
            if (run != null) {
                run.cancel();
            }
            TAGGERS.put(profile, new BukkitRunnable() {
                @Override
                public void run() {
                    TAGGERS.remove(profile);
                }
            });
            TAGGERS.get(profile).runTaskLater(BetonQuest.getInstance(), delay * 20L);
        }
    }

    /**
     * Removes the "in combat" tag.
     *
     * @param event the event to listen
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final PlayerDeathEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getEntity());
        final BukkitRunnable runnable = TAGGERS.remove(onlineProfile);
        if (runnable != null) {
            runnable.cancel();
        }
    }
}
