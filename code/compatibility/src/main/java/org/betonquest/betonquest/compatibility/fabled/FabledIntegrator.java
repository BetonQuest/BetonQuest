package org.betonquest.betonquest.compatibility.fabled;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledClassConditionFactory;
import org.betonquest.betonquest.compatibility.fabled.condition.FabledLevelConditionFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Integrator for Fabled.
 */
public class FabledIntegrator extends IntegrationTemplate {

    /**
     * Creates a new Integrator.
     */
    public FabledIntegrator() {
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {
        playerCondition("class", new FabledClassConditionFactory());
        playerCondition("level", new FabledLevelConditionFactory());

        registerFeatures(api, "fabled");

        api.bukkit().registerEvents(new FabledKillListener(api.profiles()));
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
