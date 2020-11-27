/*
 * Created on 01.07.2018.
 */
package pl.betoncraft.betonquest.exceptions;

import org.bukkit.plugin.Plugin;

/**
 * Thrown if BetonQuest tries to hook a version of a plugin that is not
 * supported
 */
public class UnsupportedVersionException extends HookException {

    private static final long serialVersionUID = 7965553395053833302L;

    /**
     * The version of the running plugin
     */
    private final String currentVersion;
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
        super(plugin, String.format("Version %s is not supported. Please install version %s!",
                plugin.getDescription().getVersion(),
                requiredVersion));
        this.currentVersion = plugin.getDescription().getVersion();
        this.requiredVersion = requiredVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getRequiredVersion() {
        return requiredVersion;
    }
}
