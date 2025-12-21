package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Support for Hologram plugins should come from implementation this abstract class. There may be multiple
 * HologramIntegrator objects loaded at once, hence reload(), and close() should not do anything.
 */
public abstract class HologramIntegrator implements Integrator, Comparable<HologramIntegrator> {

    /**
     * The name of the plugin.
     */
    private final String pluginName;

    /**
     * The minimum required version.
     */
    private final String requiredVersion;

    /**
     * The qualifiers to parse the minimum required version.
     */
    private final String[] qualifiers;

    /**
     * The plugin hooked by this integrator.
     */
    @Nullable
    private Plugin plugin;

    /**
     * Create a sub-integrator representing a specific implementation of BetonHolograms.
     *
     * @param pluginName      The plugin to be hooked
     * @param requiredVersion The minimum required version
     * @param qualifiers      Version qualifiers
     */
    public HologramIntegrator(final String pluginName, final String requiredVersion, final String... qualifiers) {
        this.pluginName = pluginName;
        this.requiredVersion = requiredVersion;
        this.qualifiers = qualifiers.clone();
    }

    /**
     * Get the name of the plugin.
     *
     * @return The name of the plugin.
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Get the plugin.
     *
     * @return the {@link Plugin} object.
     */
    @Nullable
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get the priority of this integrator based on the plugin name.
     *
     * @return The priority of this integrator ranging from 1 to the amount of HologramIntegrators, or 0 if a config option
     * did not exist or if the plugin was not found.
     */
    public int getPriority() {
        final String defaultHolograms = BetonQuest.getInstance().getPluginConfig().getString("hologram.default");
        if (defaultHolograms != null) {
            final String[] split = defaultHolograms.split(",");
            for (int i = 0; i < split.length; i++) {
                if (split[i].equalsIgnoreCase(pluginName)) {
                    return split.length - i;
                }
            }
        }
        return 0;
    }

    /**
     * Create a BetonHologram object which wraps this specific integrator's plugin's hologram.
     *
     * @param location The location of where to create the hologram
     * @return A new BetonHologram object
     */
    public abstract BetonHologram createHologram(Location location);

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null) {
            final Version version = new Version(plugin.getDescription().getVersion());
            final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, qualifiers);
            if (comparator.isOlderThan(version, new Version(requiredVersion))) {
                throw new UnsupportedVersionException(plugin, requiredVersion);
            }
        }
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void reload() {
        // Empty
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void close() {
        // Empty
    }

    /**
     * Parses a string containing a placeholder and converts it to the appropriate format for the given
     * plugin implementation.
     *
     * @param pack the quest pack where the placeholder resides
     * @param text the raw text
     * @return the parsed and formatted full string
     */
    public abstract String parsePlaceholder(QuestPackage pack, String text);

    @Override
    public int compareTo(final HologramIntegrator integrator) {
        return Integer.compare(integrator.getPriority(), this.getPriority());
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final HologramIntegrator that = (HologramIntegrator) object;
        return Objects.equals(pluginName, that.pluginName)
                && Objects.equals(requiredVersion, that.requiredVersion)
                && Objects.deepEquals(qualifiers, that.qualifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pluginName, requiredVersion, Arrays.hashCode(qualifiers));
    }
}
