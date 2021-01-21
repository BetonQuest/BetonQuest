package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.utils.LogUtils;

import java.util.logging.Level;


@SuppressWarnings("PMD.CommentRequired")
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
            LogUtils.getLogger().log(Level.FINE, "Enabled MCMMO QuestItemHandler");
        } catch (LinkageError e) {
            LogUtils.getLogger().log(Level.WARNING, "MCMMO version is not compatible with the QuestItemHandler.");
            LogUtils.logThrowable(e);
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
