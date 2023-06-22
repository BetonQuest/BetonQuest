package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;

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
        plugin.registerEvents("particle", ParticleEvent.class);
    }

    @Override
    public void postHook() throws HookException {
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        particleManager = new EffectLibParticleManager(loggerFactory, loggerFactory.create(EffectLibParticleManager.class));
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
