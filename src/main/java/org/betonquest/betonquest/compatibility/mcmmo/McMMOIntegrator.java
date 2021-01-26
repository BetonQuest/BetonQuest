package org.betonquest.betonquest.compatibility.mcmmo;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class McMMOIntegrator implements Integrator {

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
            LOG.debug(null, "Enabled MCMMO QuestItemHandler");
        } catch (final LinkageError e) {
            LOG.warning(null, "MCMMO version is not compatible with the QuestItemHandler.", e);
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
