package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.bukkit.event.npc.NpcVisibilityUpdateEvent;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * A listener class for bukkit events that holograms use.
 */
public class HologramListener implements Listener {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Npc hologram loop to notify on external triggers.
     */
    private final NpcHologramLoop npcHologramLoop;

    /**
     * Creates a new HologramListener.
     *
     * @param profileProvider the profile provider instance
     * @param npcHologramLoop the npc hologram loop to notify on external triggers
     */
    public HologramListener(final ProfileProvider profileProvider, final NpcHologramLoop npcHologramLoop) {
        this.profileProvider = profileProvider;
        this.npcHologramLoop = npcHologramLoop;
    }

    /**
     * Refreshes Holograms when a player joins the server.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        HologramRunner.refresh(profileProvider.getProfile(event.getPlayer()));
    }

    /**
     * Refreshes Holograms when a player leaves the server.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        HologramRunner.remove(event.getPlayer());
    }

    /**
     * Update the npc holograms on external triggers.
     *
     * @param event The event.
     */
    @EventHandler
    public void onExternalUpdate(final NpcVisibilityUpdateEvent event) {
        npcHologramLoop.onExternalUpdate(event.getNpc());
    }
}
