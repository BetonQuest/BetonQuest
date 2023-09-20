package pl.betoncraft.betonquest.compatibility.mmogroup.mmolib;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class MythicLibIntegrator implements Integrator {

    private final BetonQuest plugin;

    public MythicLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("mmostat", MythicLibStatCondition.class);
        plugin.registerObjectives("mmoskill", MythicLibSkillObjective.class);
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
