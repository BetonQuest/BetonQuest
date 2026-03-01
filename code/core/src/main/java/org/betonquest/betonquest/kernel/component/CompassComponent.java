package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.id.compass.CompassIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link CompassProcessor}.
 */
public class CompassComponent extends AbstractCoreComponent {

    /**
     * Create a new CompassComponent.
     */
    public CompassComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, Identifiers.class, Instructions.class,
                ParsedSectionTextCreator.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(CompassIdentifierFactory.class, CompassProcessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final CompassIdentifierFactory compassIdentifierFactory = new CompassIdentifierFactory(questPackageManager);
        identifiers.register(CompassIdentifier.class, compassIdentifierFactory);
        final CompassProcessor compassProcessor = new CompassProcessor(loggerFactory.create(CompassProcessor.class),
                instructions, parsedSectionTextCreator, compassIdentifierFactory);

        dependencyProvider.take(CompassIdentifierFactory.class, compassIdentifierFactory);
        dependencyProvider.take(CompassProcessor.class, compassProcessor);

        processorDataLoader.addProcessor(compassProcessor);
    }
}
