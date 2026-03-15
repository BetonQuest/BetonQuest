package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;

/**
 * Integrator for McMMO.
 */
public class McMMOIntegrator implements Integrator {

    /**
     * Creates a new Integrator.
     */
    public McMMOIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        api.conditions().registry().register("mcmmolevel", new McMMOSkillLevelConditionFactory());
        api.actions().registry().register("mcmmoexp", new McMMOAddExpActionFactory());
        final BetonQuestLogger log = api.loggerFactory().create(McMMOIntegrator.class);
        try {
            api.bukkit().registerEvents(new MCMMOQuestItemHandler());
            log.debug("Enabled MCMMO QuestItemHandler");
        } catch (final LinkageError e) {
            log.warn("MCMMO version is not compatible with the QuestItemHandler.", e);
        }
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
