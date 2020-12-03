package pl.betoncraft.betonquest.compatibility.worldguard;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.citizens.NPCRegionCondition;


@SuppressWarnings("PMD.CommentRequired")
public class WorldGuardIntegrator implements Integrator {

    private final BetonQuest plugin;

    public WorldGuardIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("region", RegionCondition.class);
        plugin.registerObjectives("region", RegionObjective.class);
        if (Compatibility.getHooked().contains("Citizens")) {
            plugin.registerConditions("npcregion", NPCRegionCondition.class);
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
