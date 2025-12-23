package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.Integrator;

/**
 * Integrates MythicLib.
 * MythicLib not affiliated with MythicCraft. It is part of Phoenix developments' MMO plugin suite.
 */
public class MythicLibIntegrator implements Integrator {

    /**
     * Creates a new MythicLib integrator.
     */
    public MythicLibIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        questRegistries.condition().register("mmostat", new MythicLibStatConditionFactory());
        questRegistries.objective().register("mmoskill", new MythicLibSkillObjectiveFactory());
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
