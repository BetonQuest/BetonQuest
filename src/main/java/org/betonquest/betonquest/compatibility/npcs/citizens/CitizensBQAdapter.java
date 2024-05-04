package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Citizens Compatibility Adapter for general BetonQuest NPC behaviour.
 */
public class CitizensBQAdapter implements BQNPCAdapter {
    /**
     * The Citizens NPC instance.
     */
    private final NPC npc;

    /**
     * Create a new Citizens NPC Adapter.
     *
     * @param npc the Citizens NPC instance
     */
    public CitizensBQAdapter(final NPC npc) {
        this.npc = npc;
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public String getFullName() {
        return npc.getFullName();
    }

    @Override
    public Location getLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public void teleport(final Location location) {
        CitizensIntegrator.getCitizensMoveInstance().stopNPCMoving(npc);
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned()) {
            npc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            npc.spawn(location, SpawnReason.PLUGIN);
        }
    }
}
