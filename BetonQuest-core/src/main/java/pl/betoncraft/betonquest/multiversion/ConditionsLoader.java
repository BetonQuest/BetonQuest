package pl.betoncraft.betonquest.multiversion;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.conditions.FlyingCondition;

/**
 * This class load Conditions, that depending on the minecraft version
 */
public final class ConditionsLoader {

    private ConditionsLoader() {}

    /**
     * Load the version depending Conditions
     * 
     * @param plugin
     *            The plugin instance
     */
    public static void load(final BetonQuest plugin) {
        // 1.9
        plugin.registerConditions("fly", FlyingCondition.class);
    }
}
