package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.bukkit.Location;

/**
 * NPC Plugin Adapter for general BetonQuest NPC behaviour.
 */
public interface BQNPCAdapter {
    /**
     * Gets the name of the NPC.
     *
     * @return the name without formatting
     */
    String getName();

    /**
     * Gets the formatted name of the npc.
     *
     * @return the name of the npc inclusive {@link org.bukkit.ChatColor} formatting codes.
     */
    String getFormattedName();

    /**
     * Gets the position of the NPC.
     *
     * @return the location the NPC is at
     */
    Location getLocation();

    /**
     * Teleports the NPC to a position or spawns it there.
     *
     * @param location the new location of the NPC
     */
    void teleport(Location location);
}
