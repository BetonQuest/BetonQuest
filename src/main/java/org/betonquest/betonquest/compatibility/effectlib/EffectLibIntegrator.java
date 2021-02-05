package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.citizens.CitizensParticle;


@SuppressWarnings("PMD.CommentRequired")
public class EffectLibIntegrator implements Integrator {

    private static EffectLibIntegrator instance;
    private final BetonQuest plugin;
    private EffectManager manager;

    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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
