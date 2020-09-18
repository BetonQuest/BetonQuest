package pl.betoncraft.betonquest.compatibility.mcmmo;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;


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

    }

    @Override
    public void close() {

    }

}
