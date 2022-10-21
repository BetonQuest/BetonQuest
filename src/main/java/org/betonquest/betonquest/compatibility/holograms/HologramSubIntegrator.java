package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.exceptions.HookException;
import org.betonquest.betonquest.exceptions.UnsupportedVersionException;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.betonquest.betonquest.modules.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class HologramSubIntegrator {
    private final String pluginName;
    private final Class<? extends BetonHologram> hologramType;
    private final String requiredVersion;
    private final String[] qualifiers;

    public HologramSubIntegrator(final String pluginName, final Class<? extends BetonHologram> hologramType, final String requiredVersion, final String... qualifiers) {
        this.pluginName = pluginName;
        this.hologramType = hologramType;
        this.requiredVersion = requiredVersion;
        this.qualifiers = qualifiers;
    }

    /**
     * Called which may setup dependencies or api within the hooked hologram plugin
     */
    protected void init() throws HookException {
        validateVersion();
    }

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

    public String getPluginName() {
        return pluginName;
    }

    public Class<? extends BetonHologram> getHologramType() {
        return hologramType;
    }
}
