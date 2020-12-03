package pl.betoncraft.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Compatibility;
import pl.betoncraft.betonquest.compatibility.Integrator;
import pl.betoncraft.betonquest.compatibility.citizens.CitizensParticle;


@SuppressWarnings("PMD.CommentRequired")
public class EffectLibIntegrator implements Integrator {

    private static EffectLibIntegrator instance;
    private final BetonQuest plugin;
    private EffectManager manager;

    public EffectLibIntegrator() {
        instance = this;
        plugin = BetonQuest.getInstance();
    }

    /**
     * @return the EffectLib effect manager
     */
    public static EffectManager getEffectManager() {
        return instance.manager;
    }

    @Override
    public void hook() {
        manager = new EffectManager(BetonQuest.getInstance());
        if (Compatibility.getHooked().contains("Citizens")) {
            new CitizensParticle();
        }
        plugin.registerEvents("particle", ParticleEvent.class);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        if (manager != null) {
            manager.dispose();
        }
    }

}
