package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.effectlib.action.ParticleActionFactory;
import org.betonquest.betonquest.compatibility.effectlib.identifier.ParticleIdentifier;
import org.betonquest.betonquest.compatibility.effectlib.identifier.ParticleIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for <a href="https://github.com/elBukkit/EffectLib/">EffectLib</a>.
 */
public class EffectLibIntegrator implements Integration {

    /**
     * The minimum required version of EffectLib.
     */
    public static final String REQUIRED_VERSION = "10.3";

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The processor data loader.
     */
    private final ProcessorDataLoader processorDataLoader;

    /**
     * Effect manager starting and controlling effects.
     */
    @Nullable
    private EffectManager manager;

    /**
     * The default Constructor.
     *
     * @param plugin              the plugin instance
     * @param processorDataLoader the processor data loader
     */
    public EffectLibIntegrator(final Plugin plugin, final ProcessorDataLoader processorDataLoader) {
        this.plugin = plugin;
        this.processorDataLoader = processorDataLoader;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        manager = new EffectManager(plugin);
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final ParticleIdentifierFactory factory = new ParticleIdentifierFactory(api.packages());
        api.identifiers().register(ParticleIdentifier.class, factory);
        api.actions().registry().register("particle", new ParticleActionFactory(manager));
        processorDataLoader.addProcessor(new EffectLibParticleManager(loggerFactory.create(EffectLibParticleManager.class), loggerFactory,
                api.profiles(), api.instructions(), factory, api.npcs().manager(), api.conditions().manager(), manager, plugin));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        if (manager != null) {
            manager.dispose();
        }
    }
}
