package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("PMD.CommentRequired")
public abstract class HologramSubIntegrator {
    private final String pluginName;
    private final Class<? extends BetonHologram> hologramType;
    private final String requiredVersion;
    private final String[] qualifiers;

    /**
     * Create a sub-integrator representing a specific implementation of BetonHolograms
     *
     * @param pluginName      The plugin to be hooked
     * @param hologramType    The plugin-specific wrapper implementation of a BetonHologram
     * @param requiredVersion The minimum required version
     * @param qualifiers      Version qualifiers
     */
    public HologramSubIntegrator(final String pluginName, final Class<? extends BetonHologram> hologramType, final String requiredVersion, final String... qualifiers) {
        this.pluginName = pluginName;
        this.hologramType = hologramType;
        this.requiredVersion = requiredVersion;
        this.qualifiers = qualifiers.clone();
    }

    /**
     * Called which may setup dependencies or api within the hooked hologram plugin
     *
     * @throws HookException If hooked plugin version is invalid
     */
    protected void init() throws HookException {
        validateVersion();
    }

    /**
     * Validate the version based on object parameters
     *
     * @throws UnsupportedVersionException If hooked plugin version is invalid
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            final Version version = new Version(plugin.getDescription().getVersion());
            final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, qualifiers);
            if (comparator.isOtherNewerThanCurrent(version, new Version(requiredVersion))) {
                throw new UnsupportedVersionException(plugin, requiredVersion);
            }
        }
    }

    /**
     * Parses a string containing an instruction variable and converts it to the appropriate format for the given
     * plugin implementation
     *
     * @param pack The quest pack where the variable resides
     * @param text The raw text
     * @return The parsed and formatted full string
     */
    public abstract String parseVariable(QuestPackage pack, String text);

    /**
     * @return The plugin's name
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Get this plugin's specific hologram wrapper implementation
     *
     * @return The class ? extending BetonHologram
     */
    public Class<? extends BetonHologram> getHologramType() {
        return hologramType;
    }
}
