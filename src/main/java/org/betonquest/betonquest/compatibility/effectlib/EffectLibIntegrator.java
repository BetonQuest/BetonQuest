package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.effectlib.event.ParticleEventFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for <a href="https://github.com/elBukkit/EffectLib/">EffectLib</a>.
 */
public class EffectLibIntegrator implements Integrator {
    /**
     * BetonQuest plugin.
     */
    private final BetonQuest plugin;

    /**
     * Effect manager starting and controlling effects.
     */
    @Nullable
    private EffectManager manager;

    /**
     * Particle Manager displaying effects on NPCs.
     */
    @Nullable
    private EffectLibParticleManager particleManager;

    /**
     * The default Constructor.
     */
    public EffectLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        manager = new EffectManager(plugin);
        final PrimaryServerThreadData data = new PrimaryServerThreadData(plugin.getServer(), plugin.getServer().getScheduler(), plugin);
        plugin.getQuestRegistries().event().register("particle", new ParticleEventFactory(plugin.getLoggerFactory(), data, manager));
    }

    @Override
    public void postHook() {
        if (manager != null) {
            final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
            particleManager = new EffectLibParticleManager(loggerFactory, loggerFactory.create(EffectLibParticleManager.class), manager, plugin.getNpcProcessor());
        }
    }

    @Override
    public void reload() {
        if (particleManager != null) {
            particleManager.reload();
        }
    }

    @Override
    public void close() {
        if (manager != null) {
            manager.dispose();
        }
    }
}
