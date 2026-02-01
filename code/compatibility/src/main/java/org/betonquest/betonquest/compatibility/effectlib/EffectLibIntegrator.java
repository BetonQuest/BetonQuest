package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.effectlib.action.ParticleActionFactory;
import org.betonquest.betonquest.compatibility.effectlib.identifier.ParticleIdentifier;
import org.betonquest.betonquest.compatibility.effectlib.identifier.ParticleIdentifierFactory;
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
        final ParticleIdentifierFactory factory = new ParticleIdentifierFactory(api.getQuestPackageManager());
        api.getQuestRegistries().identifier().register(ParticleIdentifier.class, factory);
        api.getQuestRegistries().action().register("particle", new ParticleActionFactory(loggerFactory, manager));
        plugin.addProcessor(new EffectLibParticleManager(loggerFactory.create(EffectLibParticleManager.class), loggerFactory,
                api.getQuestTypeApi(), api.getFeatureApi(), api.getProfileProvider(),
                api.getInstructionApi(), factory, manager, plugin));
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
