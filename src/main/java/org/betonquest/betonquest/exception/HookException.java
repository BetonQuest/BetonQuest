package org.betonquest.betonquest.exception;

import org.bukkit.plugin.Plugin;

import java.io.Serial;

/**
 * Thrown if BetonQuest tries to hook a plugin.
 */
public class HookException extends Exception {
    @Serial
    private static final long serialVersionUID = 7965553395053833302L;

    /**
     * The related plugin.
     */
    private final Plugin plugin;

    /**
     * Constructs a new exception related to a plugin.
     * {@link Exception#Exception(String)}
     *
     * @param plugin  The plugin
     * @param message The Message.
     */
    public HookException(final Plugin plugin, final String message) {
        super(message);
        this.plugin = plugin;
    }

    /**
     * Constructs a new exception related to a plugin.
     * {@link Exception#Exception(String, Throwable)}
     *
     * @param plugin  The plugin
     * @param message The message
     * @param cause   The Throwable
     */
    public HookException(final Plugin plugin, final String message, final Throwable cause) {
        super(message, cause);
        this.plugin = plugin;
    }

    /**
     * Constructs a new exception related to a plugin.
     * {@link Exception#Exception(Throwable)}
     *
     * @param plugin The plugin
     * @param cause  the exceptions cause.
     */
    public HookException(final Plugin plugin, final Throwable cause) {
        super(cause);
        this.plugin = plugin;
    }

    /**
     * Get the {@link Plugin}.
     *
     * @return related plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get the name of the {@link Plugin}.
     *
     * @return name of related plugin
     */
    public String getPluginName() {
        return plugin.getName();
    }

    /**
     * Get the version of the {@link Plugin}.
     *
     * @return version of related plugin
     */
    public String getPluginVersion() {
        return getPlugin().getDescription().getVersion();
    }
}
