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
        plugin.getQuestRegistries().getConditionTypes().register("mmostat", MythicLibStatCondition.class);

        plugin.registerObjectives("mmoskill", MythicLibSkillObjective.class);
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
