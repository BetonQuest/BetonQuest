package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.FunctionIdentifier;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.function.Functions;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.particle.ParticleIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.feature.ParticleProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.particle.datafunction.BlockDataProvider;
import org.betonquest.betonquest.particle.datafunction.DustOptionsProvider;
import org.betonquest.betonquest.particle.datafunction.ItemStackProvider;
import org.betonquest.betonquest.particle.datafunction.TransitionOptionsProvider;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ParticleProcessor}.
 */
public class ParticlesComponent extends AbstractCoreComponent {

    /**
     * Create a new ParticlesComponent.
     */
    public ParticlesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, Identifiers.class,
                Instructions.class, Plugin.class, Functions.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ParticleIdentifierFactory.class, ParticleProcessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Plugin plugin = getDependency(Plugin.class);
        final Functions functions = getDependency(Functions.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final ParticleIdentifierFactory particleIdentifierFactory = new ParticleIdentifierFactory(packManager);
        identifiers.register(ParticleIdentifier.class, particleIdentifierFactory);

        final ParticleProcessor particleProcessor;
        try {
            particleProcessor = new ParticleProcessor(
                    loggerFactory.create(ParticleProcessor.class),
                    instructions,
                    particleIdentifierFactory,
                    identifiers.getFactory(FunctionIdentifier.class),
                    plugin,
                    functions
            );
        } catch (final QuestException e) {
            throw new IllegalStateException("Could not load ParticleProcessor", e);
        }

        particleProcessor.addDataFunctionProvider(new DustOptionsProvider());
        particleProcessor.addDataFunctionProvider(new BlockDataProvider());
        particleProcessor.addDataFunctionProvider(new ItemStackProvider());
        particleProcessor.addDataFunctionProvider(new TransitionOptionsProvider());

        dependencyProvider.take(ParticleIdentifierFactory.class, particleIdentifierFactory);
        dependencyProvider.take(ParticleProcessor.class, particleProcessor);

        processorDataLoader.addProcessor(particleProcessor);
    }
}
