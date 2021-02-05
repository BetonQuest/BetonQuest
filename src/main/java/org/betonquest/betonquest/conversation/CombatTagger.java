package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.BetonQuest;
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
import java.util.Map;

/**
 * Tags players that are in combat to prevent them from starting the
 * conversation
 */
@SuppressWarnings("PMD.CommentRequired")
public class CombatTagger implements Listener {

    private static final Map<String, Boolean> TAGGED = new HashMap<>();
    private static final Map<String, BukkitRunnable> UNTAGGERS = new HashMap<>();
    private final int delay;

    /**
     * Starts the combat listener
     */
    public CombatTagger() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        delay = Integer.parseInt(Config.getString("config.combat_delay"));
    }

    /**
     * Checks if the player is combat-tagged
     *
     * @param playerID ID of the player
     * @return true if the player is tagged, false otherwise
     */
    public static boolean isTagged(final String playerID) {
        boolean result = false;
        final Boolean state = TAGGED.get(playerID);
        if (state != null) {
            result = state;
        }
        return result;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final ArrayList<String> ids = new ArrayList<>();
        if (event.getEntity() instanceof Player) {
            ids.add(PlayerConverter.getID((Player) event.getEntity()));
        }
        if (event.getDamager() instanceof Player) {
            ids.add(PlayerConverter.getID((Player) event.getDamager()));
        }
        for (final String playerID : ids) {
            TAGGED.put(playerID, true);
            final BukkitRunnable run = UNTAGGERS.get(playerID);
            if (run != null) {
                run.cancel();
            }
            UNTAGGERS.put(playerID, new BukkitRunnable() {
                @Override
                public void run() {
                    TAGGED.put(playerID, false);
                }
            });
            UNTAGGERS.get(playerID).runTaskLater(BetonQuest.getInstance(), delay * 20L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(final PlayerDeathEvent event) {
        final String playerID = PlayerConverter.getID(event.getEntity());
        TAGGED.remove(playerID);
        final BukkitRunnable runnable = UNTAGGERS.remove(playerID);
        if (runnable != null) {
            runnable.cancel();
        }
    }
}
