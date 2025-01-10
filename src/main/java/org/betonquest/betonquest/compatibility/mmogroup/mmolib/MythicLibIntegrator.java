package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

/**
 * Integrates MythicLib.
 * MythicLib not affiliated with MythicCraft. It is part of Phoenix developments' MMO plugin suite.
 */
public class MythicLibIntegrator implements Integrator {

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Creates a new MythicLib integrator.
     */
    public MythicLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.getQuestRegistries().condition().register("mmostat", MythicLibStatCondition.class);

        plugin.getQuestRegistries().objective().register("mmoskill", MythicLibSkillObjective.class);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
