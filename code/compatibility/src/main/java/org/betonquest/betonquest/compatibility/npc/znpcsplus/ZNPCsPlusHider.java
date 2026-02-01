package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.event.NpcSpawnEvent;
import org.betonquest.betonquest.api.quest.npc.NpcHider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Prevents respawning of hidden Npcs.
 */
public class ZNPCsPlusHider implements Listener {

    /**
     * Hider to check current visibility.
     */
    private final NpcHider npcHider;

    /**
     * Create a new ZNPCsPlus Hider to force Npc hiding.
     *
     * @param npcHider the npc hider to check if Npc is hidden
     */
    public ZNPCsPlusHider(final NpcHider npcHider) {
        this.npcHider = npcHider;
    }

    /**
     * Cancels npc sending to player if the hide conditions are met.
     *
     * @param event the spawn event to listen
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpawn(final NpcSpawnEvent event) {
        if (npcHider.isHidden(new ZNPCsPlusAdapter(event.getEntry()), event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
