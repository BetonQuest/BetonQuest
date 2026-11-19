package org.betonquest.betonquest.mc_1_20_6;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.item.SimpleQuestItemHandlerRegistry;
import org.betonquest.betonquest.kernel.processor.feature.ParticleProcessor;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedNameHandler;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedPotionHandler;
import org.betonquest.betonquest.mc_1_20_6.particle.datafunction.ColorProvider;
import org.betonquest.betonquest.mc_1_20_6.particle.datafunction.FloatProvider;
import org.betonquest.betonquest.mc_1_20_6.particle.datafunction.IntegerProvider;
import org.betonquest.betonquest.mc_1_20_6.particle.datafunction.VibrationProvider;

/**
 * Allows to register features with Minecraft 1.20.6.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_20_6 implements Integration {

    /**
     * BetonQuest class to get relevant object from.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public BundledMC_1_20_6(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final SimpleQuestItemHandlerRegistry handlerRegistry = betonQuest.getComponentLoader().get(SimpleQuestItemHandlerRegistry.class);
        handlerRegistry.register(new UpdatedNameHandler());
        handlerRegistry.register(new UpdatedPotionHandler());
        final ParticleProcessor particleProcessor = betonQuest.getComponentLoader().get(ParticleProcessor.class);
        particleProcessor.addDataFunctionProvider(new VibrationProvider());
        particleProcessor.addDataFunctionProvider(new ColorProvider());
        particleProcessor.addDataFunctionProvider(new FloatProvider());
        particleProcessor.addDataFunctionProvider(new IntegerProvider());
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
