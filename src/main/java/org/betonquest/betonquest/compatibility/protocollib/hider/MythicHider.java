package org.betonquest.betonquest.compatibility.protocollib.hider;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
@CustomLog
public final class MythicHider extends BukkitRunnable implements Listener {

    private static MythicHider instance;

    private final EntityHider hider;
    private final Map<Entity, Set<UUID>> mythicmobs;

    private MythicHider() {
        super();
        mythicmobs = new HashMap<>();
        final int updateInterval = BetonQuest.getInstance().getPluginConfig().getInt("npc_hider_check_interval", 5 * 20);
        hider = new EntityHider(BetonQuest.getInstance(), EntityHider.Policy.BLACKLIST);
        runTaskTimer(BetonQuest.getInstance(), 0, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Starts (or restarts) the NPCHider. It loads the current configuration for hidden NPCs
     */
    public static void start() {
        if (instance != null) {
            instance.stop();
        }
        instance = new MythicHider();
    }

    /**
     * @return the currently used NPCHider instance
     */
    public static MythicHider getInstance() {
        return instance;
    }

    @Override
    public void run() {
        applyVisibility();
    }

    /**
     * Stops the NPCHider, cleaning up all listeners, runnables etc.
     */
    public void stop() {
        hider.close();
        cancel();
        HandlerList.unregisterAll(this);
    }

    /**
     * Updates the visibility of tracked mobs for all players.
     */
    public void applyVisibility() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final Entity mob : mythicmobs.keySet()) {
                applyVisibility(p, mob);
            }
        }
    }

    /**
     * Updates the visibility of all MythicMobs for this player.
     *
     * @param player the player
     */
    public void applyVisibility(final Player player) {
        for (final Map.Entry<Entity, Set<UUID>> mythic : mythicmobs.entrySet()) {
            if (!mythic.getValue().contains(player.getUniqueId())) {
                hider.hideEntity(player, mythic.getKey());
            }
        }
    }

    /**
     * Updates the visibility of the specified MythicMob for this player.
     *
     * @param player
     * @param mythicMob
     */
    public void applyVisibility(final Player player, final Entity mythicMob) {
        if (!mythicmobs.get(mythicMob).contains(player.getUniqueId())) {
            hider.hideEntity(player, mythicMob);
        }
    }

    /**
     * Updates the visibility of this MythicMob for all players.
     *
     * @param mythicMob
     */
    public void applyVisibility(final Entity mythicMob) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            applyVisibility(p, mythicMob);
        }
    }

    /**
     * Hides the mythicMob from all players besides the passed in one.
     *
     * @param player    the player who you want to show the mob to
     * @param mythicMob the mob that you want to hide from other players
     */
    public void applyVisibilityPrivate(final Player player, final Entity mythicMob) {
        final Set<UUID> playerUUIDS = new HashSet<>();
        playerUUIDS.add(player.getUniqueId());
        mythicmobs.put(mythicMob, playerUUIDS);
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) { //Hiding the mob for all players besides passed in online
            if (!onlinePlayer.equals(player)) {
                applyVisibility(onlinePlayer, mythicMob);
            }
        }
    }

    /**
     * Checks whether the MythicMob is visible to the player.
     *
     * @param player    the player
     * @param mythicMob ID of the NPC
     * @return true if the NPC is visible to that player, false otherwise
     */
    public boolean isInvisible(final Player player, final Entity mythicMob) {
        if (mythicMob == null) {
            return false;
        }
        return !hider.isVisible(player, mythicMob.getEntityId());
    }

    /**
     * Checks if the player logging in can see any of the mobs in the list of tracked mobs, if not hides the mob
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        applyVisibility(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMythicKill(final MythicMobDeathEvent event) {
        mythicmobs.remove(event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMythicDespawn(final MythicMobDespawnEvent event) {
        mythicmobs.remove(event.getEntity());
    }
}
