package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.integration.Integration;
import org.bukkit.Location;

import java.util.Objects;

/**
 * Support for Hologram plugins should come from implementation this abstract class. There may be multiple
 * HologramIntegrator objects loaded at once, hence reload(), and close() should not do anything.
 */
public abstract class HologramIntegrator implements Integration, Comparable<HologramIntegrator> {

    /**
     * The name of the plugin.
     */
    private final String pluginName;

    /**
     * The minimum required version.
     */
    private final String requiredVersion;

    /**
     * Create a sub-integrator representing a specific implementation of BetonHolograms.
     *
     * @param pluginName      The plugin to be hooked
     * @param requiredVersion The minimum required version
     */
    public HologramIntegrator(final String pluginName, final String requiredVersion) {
        this.pluginName = pluginName;
        this.requiredVersion = requiredVersion;
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
    public final void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public final void disable() {
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
                && Objects.equals(requiredVersion, that.requiredVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pluginName, requiredVersion);
    }
}
