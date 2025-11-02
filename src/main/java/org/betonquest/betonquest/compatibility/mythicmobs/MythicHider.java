package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Hides MythicMobs from the spawn event and NPCs.
 */
public final class MythicHider implements Listener {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Plugin instance to hide.
     */
    private final Plugin plugin;

    /**
     * Mobs with a list of player uuids who are allowed to see them.
     */
    private final Map<Entity, Set<UUID>> mythicmobs;

    /**
     * The currently active hide loop.
     */
    @Nullable
    private BukkitTask loop;

    /**
     * Creates a new mythic mobs hider.
     *
     * @param profileProvider the profile provider
     * @param plugin          the plugin to start the loop
     */
    public MythicHider(final ProfileProvider profileProvider, final Plugin plugin) {
        super();
        this.profileProvider = profileProvider;
        this.plugin = plugin;
        mythicmobs = new HashMap<>();
    }

    /**
     * Re-/Loads the hide loop.
     *
     * @param updateInterval the interval to update stored mobs visibility
     */
    public void reload(final int updateInterval) {
        stop();
        loop = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
                for (final Entity mob : mythicmobs.keySet()) {
                    applyVisibility(onlineProfile, mob);
                }
            }
        }, 0, updateInterval);
    }

    /**
     * Stops the NPCHider, cleaning up all runnables.
     */
    public void stop() {
        if (loop != null) {
            loop.cancel();
            loop = null;
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
                onlineProfile.getPlayer().hideEntity(plugin, mythic.getKey());
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
            onlineProfile.getPlayer().hideEntity(plugin, mythicMob);
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
        for (final OnlineProfile onlinePlayer : profileProvider.getOnlineProfiles()) { // Hiding the mob for all players besides passed in online
            if (!onlinePlayer.equals(onlineProfile)) {
                applyVisibility(onlinePlayer, mythicMob);
            }
        }
    }

    /**
     * Hides the mob from the player.
     *
     * @param onlineProfile the onlinePlayer of the player
     * @param mythicMob     the mob to hide
     */
    public void hide(final OnlineProfile onlineProfile, final Entity mythicMob) {
        final Set<UUID> uuids = mythicmobs.get(mythicMob);
        if (uuids != null) {
            uuids.remove(onlineProfile.getPlayerUUID());
        }
        onlineProfile.getPlayer().hideEntity(plugin, mythicMob);
    }

    /**
     * Shows the mob again to the player.
     *
     * @param onlineProfile the onlinePlayer of the player
     * @param mythicMob     the mob to show again
     */
    public void show(final OnlineProfile onlineProfile, final Entity mythicMob) {
        final Set<UUID> uuids = mythicmobs.get(mythicMob);
        if (uuids != null) {
            uuids.add(onlineProfile.getPlayerUUID());
        }
        onlineProfile.getPlayer().showEntity(plugin, mythicMob);
    }

    /**
     * Checks if the player logging in can see any of the mobs in the list of tracked mobs, if not hides the mob.
     *
     * @param event the event of the player joining
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        applyVisibility(profileProvider.getProfile(event.getPlayer()));
    }

    /**
     * Removes the mob from the hider.
     *
     * @param event The entity death event
     */
    @EventHandler(ignoreCancelled = true)
    public void onMythicKill(final MythicMobDeathEvent event) {
        mythicmobs.remove(event.getEntity());
    }

    /**
     * Removes the mob from the hider.
     *
     * @param event The mm despawn event
     */
    @EventHandler(ignoreCancelled = true)
    public void onMythicDespawn(final MythicMobDespawnEvent event) {
        mythicmobs.remove(event.getEntity());
    }
}
