package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.effectlib.event.ParticleEventFactory;
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
     * The default Constructor.
     */
    public EffectLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        manager = new EffectManager(plugin);
        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();
        api.getQuestRegistries().event().register("particle", new ParticleEventFactory(loggerFactory, data, manager));

        plugin.addProcessor(new EffectLibParticleManager(loggerFactory.create(EffectLibParticleManager.class), loggerFactory,
                api.getQuestPackageManager(), api.getQuestTypeApi(), api.getFeatureApi(), api.getProfileProvider(),
                plugin.getVariableProcessor(), manager, plugin));
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
