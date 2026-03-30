package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Prevents respawning of hidden Npcs.
 */
public class FancyHider implements Listener {

    /**
     * The plugin instance to run tasks on.
     */
    private final Plugin plugin;

    /**
     * Manager to check current visibility.
     */
    private final NpcManager npcManager;

    /**
     * Create a new Fancy Hider to force Npc hiding.
     *
     * @param plugin     the plugin instance to run tasks on
     * @param npcManager the npc manager to check if Npc is hidden
     */
    public FancyHider(final Plugin plugin, final NpcManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    /**
     * Cancels npc sending to player if the hide conditions are met.
     *
     * @param event the spawn event to listen
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpawn(final NpcSpawnEvent event) {
        if (npcManager.isHidden(new FancyAdapter(plugin, event.getNpc()), event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
