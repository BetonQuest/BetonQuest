package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;

/**
 * Integrates MythicLib.
 * MythicLib not affiliated with MythicCraft. It is part of Phoenix developments' MMO plugin suite.
 */
public class MythicLibIntegrator implements Integration {

    /**
     * Creates a new MythicLib integrator.
     */
    public MythicLibIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        api.conditions().registry().register("mmostat", new MythicLibStatConditionFactory());
        api.objectives().registry().register("mmoskill", new MythicLibSkillObjectiveFactory());
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
