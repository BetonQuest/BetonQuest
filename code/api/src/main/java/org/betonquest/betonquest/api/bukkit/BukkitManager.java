package org.betonquest.betonquest.api.bukkit;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * The BukkitManager interface offers shortcuts for common calls to the Bukkit API.
 */
public interface BukkitManager {

    /**
     * Get the plugin instance the api instance was created for.
     *
     * @return the plugin instance of the api instance
     */
    Plugin plugin();

    /**
     * Register the given listener with all its events to the {@link PluginManager} using the plugin the api instance
     * was created for.
     *
     * @param listener the listener to register
     */
    void registerEvents(Listener listener);
}
