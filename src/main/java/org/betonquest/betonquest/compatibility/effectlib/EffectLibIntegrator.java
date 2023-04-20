package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;

@SuppressWarnings("PMD.CommentRequired")
public class EffectLibIntegrator implements Integrator {
    private static EffectLibIntegrator instance;

    private final BetonQuest plugin;

    private EffectManager manager;

    private EffectLibParticleManager particleManager;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
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
        particleManager = new EffectLibParticleManager();
        plugin.registerEvents("particle", ParticleEvent.class);
    }

    @Override
    public void reload() {
        particleManager.reload();
    }

    @Override
    public void close() {
        if (manager != null) {
            manager.dispose();
        }
    }

}
