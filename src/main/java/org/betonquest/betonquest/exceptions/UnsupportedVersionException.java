/*
 * Created on 01.07.2018.
 */
package org.betonquest.betonquest.exceptions;

import org.bukkit.plugin.Plugin;

/**
 * Thrown if BetonQuest tries to hook a version of a plugin that is not
 * supported
 */
public class UnsupportedVersionException extends HookException {

    private static final long serialVersionUID = 7965553395053833302L;

    /**
     * The supported version of the plugin
     */
    private final String requiredVersion;

    /**
     * Constructs a new exception
     *
     * @param plugin          The plugin, in the wrong version
     * @param requiredVersion The expected version
     */
    public UnsupportedVersionException(final Plugin plugin, final String requiredVersion) {
        super(plugin, String.format("%s version %s is not supported. Please install version %s!",
                plugin.getName(),
                plugin.getDescription().getVersion(),
                requiredVersion));
        this.requiredVersion = requiredVersion;
    }

    /**
     * @return Get the required version of the {@link Plugin}
     */
    public String getRequiredPluginVersion() {
        return requiredVersion;
    }
}
