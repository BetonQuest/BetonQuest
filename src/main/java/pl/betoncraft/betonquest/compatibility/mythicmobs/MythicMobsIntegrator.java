package pl.betoncraft.betonquest.compatibility.mythicmobs;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class MythicMobsIntegrator implements Integrator {

    private final BetonQuest plugin;

    public MythicMobsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("mythicmobdistance", MythicMobDistanceCondition.class);
        plugin.registerObjectives("mmobkill", MythicMobKillObjective.class);
        plugin.registerEvents("mspawnmob", MythicSpawnMobEvent.class);
    }

    @Override
    public void reload() {
    }

    @Override
    public void close() {
    }

}
