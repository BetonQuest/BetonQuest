/*
 * Created on 01.07.2018.
 */
package pl.betoncraft.betonquest.exceptions;

import org.bukkit.plugin.Plugin;

/**
 * Thrown if BetonQuest tries to hook a plugin
 */
public class HookException extends Exception {

    private static final long serialVersionUID = 7965553395053833302L;

    /**
     * The plugin
     */
    private final Plugin plugin;

    /**
     * Constructs a new exception
     *
     * @param plugin The plugin
     */
    public HookException(final Plugin plugin, final String message) {
        super(message);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
