package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrates MythicLib.
 * MythicLib not affiliated with MythicCraft. It is part of Phoenix developments' MMO plugin suite.
 */
public class MythicLibIntegrator extends IntegrationTemplate {

    /**
     * Creates a new MythicLib integrator.
     */
    public MythicLibIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        playerCondition("stat", new MythicLibStatConditionFactory());
        objective("skill", new MythicLibSkillObjectiveFactory());

        registerFeatures(api, "mmo");
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
