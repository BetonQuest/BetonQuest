package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Location;

import java.util.Optional;

/**
 * Npc Plugin Adapter for general BetonQuest Npc behaviour.
 *
 * @param <T> the original npc type
 * @since 3.0.0
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Npc<T> {

    /**
     * Gets the original object.
     *
     * @return the adapted object
     * @since 3.0.0
     */
    T getOriginal();

    /**
     * Gets the name of the Npc.
     *
     * @return the name without formatting
     * @since 3.0.0
     */
    String getName();

    /**
     * Gets the formatted name of the npc.
     *
     * @return the name of the npc inclusive {@link org.bukkit.ChatColor} formatting codes.
     * @since 3.0.0
     */
    String getFormattedName();

    /**
     * Gets the position of the Npc.
     *
     * @return the location the Npc is at, copy
     * @since 3.0.0
     */
    Optional<Location> getLocation();

    /**
     * Get a Location detailing the current eye position of the living entity.
     *
     * @return a location at the eyes of the npc
     * @since 3.0.0
     */
    Optional<Location> getEyeLocation();

    /**
     * Teleports the Npc to a position or spawns it there.
     *
     * @param location the new location of the Npc
     * @since 3.0.0
     */
    void teleport(Location location);

    /**
     * Checks if the Npc exists in a world.
     *
     * @return if the Npc exists in a world
     * @since 3.0.0
     */
    boolean isSpawned();

    /**
     * Spawns the Npc, if not already in the world.
     *
     * @param location the location to spawn the Npc at
     * @since 3.0.0
     */
    void spawn(Location location);

    /**
     * Removes the Npc from the world.
     *
     * @since 3.0.0
     */
    void despawn();

    /**
     * Shows the Npc to the player.
     *
     * @param onlineProfile the online profile of the player
     * @since 3.0.0
     */
    void show(OnlineProfile onlineProfile);

    /**
     * Hides the Npc from the player.
     *
     * @param onlineProfile the online profile of the player
     * @since 3.0.0
     */
    void hide(OnlineProfile onlineProfile);
}
