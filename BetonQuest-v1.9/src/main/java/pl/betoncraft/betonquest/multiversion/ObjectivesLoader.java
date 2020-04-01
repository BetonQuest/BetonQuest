package pl.betoncraft.betonquest.multiversion;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * This class load Objectives, that depending on the minecraft version
 */
public final class ObjectivesLoader {

    private ObjectivesLoader() {}

    /**
     * Load the version depending Objectives
     * 
     * @param plugin
     *            The plugin instance
     */
    public static void load(final BetonQuest plugin) {
        // 1.10
        // plugin.registerObjectives("breed", BreedObjective.class);
    }
}
