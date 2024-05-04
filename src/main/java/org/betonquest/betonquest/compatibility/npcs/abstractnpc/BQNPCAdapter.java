package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.bukkit.Location;

/**
 * NPC Plugin Adapter for general BetonQuest NPC behaviour.
 */
public interface BQNPCAdapter {
    String getName();

    String getFullName();

    Location getLocation();

    void teleport(Location location);
}
