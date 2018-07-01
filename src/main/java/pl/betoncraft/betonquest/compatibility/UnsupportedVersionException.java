/**
 * Created on 01.07.2018.
 *
 * @author Jonas Blocher
 */
package pl.betoncraft.betonquest.compatibility;

import org.bukkit.plugin.Plugin;

/**
 * Thrown if BetonQuest tries to hook a version of a plugin that is not supported
 * <p>
 * Created on 01.07.2018.
 *
 * @author Jonas Blocher
 */
public class UnsupportedVersionException extends Exception {

    private final String currentVersion;
    private final String requiredVersion;
    private final Plugin plugin;

    public UnsupportedVersionException(Plugin plugin, String required) {
        super(String.format("%s version %s is not supported. Please install version %s!",
                            plugin.getName(),
                            plugin.getDescription().getVersion(),
                            required));
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.requiredVersion = required;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getRequiredVersion() {
        return requiredVersion;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
