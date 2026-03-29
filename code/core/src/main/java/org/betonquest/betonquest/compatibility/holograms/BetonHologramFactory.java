package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.bukkit.Location;

/**
 * Creates {@link BetonHologram}s and converts Placeholders to the plugin specific syntax.
 */
public interface BetonHologramFactory {

    /**
     * Create a BetonHologram object which wraps this specific integrator's plugin's hologram.
     *
     * @param location the location to create the hologram at
     * @return A new BetonHologram object
     */
    BetonHologram createHologram(Location location);

    /**
     * Parses a string containing a placeholder and converts it to the appropriate format for the given
     * plugin implementation.
     *
     * @param pack the quest pack where the placeholder resides
     * @param text the raw text
     * @return the parsed and formatted full string
     */
    String parsePlaceholder(QuestPackage pack, String text);
}
