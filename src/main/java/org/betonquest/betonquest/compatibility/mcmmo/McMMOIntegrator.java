package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class McMMOIntegrator implements Integrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(McMMOIntegrator.class);

    private final BetonQuest plugin;

    public McMMOIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("mcmmolevel", McMMOSkillLevelCondition.class);
        plugin.registerEvents("mcmmoexp", McMMOAddExpEvent.class);
        try {
            new MCMMOQuestItemHandler();
            LOG.debug("Enabled MCMMO QuestItemHandler");
        } catch (final LinkageError e) {
            LOG.warn("MCMMO version is not compatible with the QuestItemHandler.", e);
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
