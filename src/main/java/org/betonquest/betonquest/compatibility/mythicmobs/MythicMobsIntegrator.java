package org.betonquest.betonquest.compatibility.mythicmobs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;

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
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            MythicHider.start();
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
