package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
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
@SuppressWarnings("PMD.CommentRequired")
public class CombatTagger implements Listener {
    private static final Map<Profile, Boolean> TAGGED = new HashMap<>();

    private static final Map<Profile, BukkitRunnable> UNTAGGERS = new HashMap<>();

    private final int delay;

    /**
     * Starts the combat listener
     */
    public CombatTagger() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        delay = Integer.parseInt(Config.getString("config.combat_delay"));
    }

    /**
     * Checks if the profile is combat-tagged.
     *
     * @param profile the {@link Profile} to check
     * @return true if the profile is tagged, false otherwise
     */
    public static boolean isTagged(final Profile profile) {
        boolean result = false;
        final Boolean state = TAGGED.get(profile);
        if (state != null) {
            result = state;
        }
        return result;
    }

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
            TAGGED.put(profile, true);
            final BukkitRunnable run = UNTAGGERS.get(profile);
            if (run != null) {
                run.cancel();
            }
            UNTAGGERS.put(profile, new BukkitRunnable() {
                @Override
                public void run() {
                    TAGGED.put(profile, false);
                }
            });
            UNTAGGERS.get(profile).runTaskLater(BetonQuest.getInstance(), delay * 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final PlayerDeathEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getEntity());
        TAGGED.remove(onlineProfile);
        final BukkitRunnable runnable = UNTAGGERS.remove(onlineProfile);
        if (runnable != null) {
            runnable.cancel();
        }
    }
}
