package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Location;

/**
 * NPC Plugin Adapter for general BetonQuest NPC behaviour.
 *
 * @param <T> the original npc type
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Npc<T> {
    /**
     * Gets the original object.
     *
     * @return the adapted object
     */
    T getOriginal();

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
     * @return the location the NPC is at, copy
     */
    Location getLocation();

    /**
     * Get a Location detailing the current eye position of the living entity.
     *
     * @return a location at the eyes of the npc
     */
    Location getEyeLocation();

    /**
     * Teleports the NPC to a position or spawns it there.
     *
     * @param location the new location of the NPC
     */
    void teleport(Location location);

    /**
     * Checks if the Npc exists in a world.
     *
     * @return if the Npc exists in a world
     */
    boolean isSpawned();

    /**
     * Spawns the Npc, if not already in the world.
     *
     * @param location the location to spawn the Npc at
     */
    void spawn(Location location);

    /**
     * Removes the Npc from the world.
     */
    void despawn();

    /**
     * Shows the Npc to the player.
     *
     * @param onlineProfile the online profile of the player
     */
    void show(OnlineProfile onlineProfile);

    /**
     * Hides the Npc from the player.
     *
     * @param onlineProfile the online profile of the player
     */
    void hide(OnlineProfile onlineProfile);
}
