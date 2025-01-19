package org.betonquest.betonquest.compatibility.protocollib.hider;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
public final class MythicHider extends BukkitRunnable implements Listener {

    @Nullable
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
    @Nullable
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
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            for (final Entity mob : mythicmobs.keySet()) {
                applyVisibility(onlineProfile, mob);
            }
        }
    }

    /**
     * Updates the visibility of all MythicMobs for this player.
     *
     * @param onlineProfile the player to update the visibility for
     */
    public void applyVisibility(final OnlineProfile onlineProfile) {
        for (final Map.Entry<Entity, Set<UUID>> mythic : mythicmobs.entrySet()) {
            if (!mythic.getValue().contains(onlineProfile.getProfileUUID())) {
                hider.hideEntity(onlineProfile, mythic.getKey());
            }
        }
    }

    /**
     * Updates the visibility of the specified MythicMob for this player.
     *
     * @param onlineProfile the {@link OnlineProfile} of the player
     * @param mythicMob     the mob to update the visibility for
     */
    public void applyVisibility(final OnlineProfile onlineProfile, final Entity mythicMob) {
        final Set<UUID> uuids = mythicmobs.get(mythicMob);
        if (uuids != null && !uuids.contains(onlineProfile.getProfileUUID())) {
            hider.hideEntity(onlineProfile, mythicMob);
        }
    }

    /**
     * Updates the visibility of this MythicMob for all players.
     *
     * @param mythicMob the mob to update the visibility for
     */
    public void applyVisibility(final Entity mythicMob) {
        for (final OnlineProfile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            applyVisibility(onlineProfile, mythicMob);
        }
    }

    /**
     * Hides the mythicMob from all players besides the passed in one.
     *
     * @param onlineProfile the onlinePlayer of the player who you want to show the mob to
     * @param mythicMob     the mob that you want to hide from other players
     */
    public void applyVisibilityPrivate(final OnlineProfile onlineProfile, final Entity mythicMob) {
        final Set<UUID> profileUUIDS = new HashSet<>();
        profileUUIDS.add(onlineProfile.getProfileUUID());
        mythicmobs.put(mythicMob, profileUUIDS);
        for (final OnlineProfile onlinePlayer : PlayerConverter.getOnlineProfiles()) { //Hiding the mob for all players besides passed in online
            if (!onlinePlayer.equals(onlineProfile)) {
                applyVisibility(onlinePlayer, mythicMob);
            }
        }
    }

    /**
     * Checks whether the MythicMob is visible to the player.
     *
     * @param onlineProfile the onlinePlayer of the player who you want to check
     * @param mythicMob     ID of the NPC
     * @return true if the NPC is visible to that player, false otherwise
     */
    public boolean isInvisible(final OnlineProfile onlineProfile, @Nullable final Entity mythicMob) {
        return mythicMob != null && !hider.isVisible(onlineProfile, mythicMob.getEntityId());
    }

    /**
     * Checks if the player logging in can see any of the mobs in the list of tracked mobs, if not hides the mob
     *
     * @param event the event of the player joining
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        applyVisibility(PlayerConverter.getID(event.getPlayer()));
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
