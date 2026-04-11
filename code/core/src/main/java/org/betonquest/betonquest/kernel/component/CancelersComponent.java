package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.cancel.QuestCancelerIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.ItemProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ObjectiveProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link CancelerProcessor}.
 */
public class CancelersComponent extends AbstractCoreComponent {

    /**
     * Create a new CancelersComponent.
     */
    public CancelersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, PlayerDataStorage.class,
                Translations.class, Identifiers.class, Instructions.class, ParsedSectionTextCreator.class,
                ActionProcessor.class, ConditionProcessor.class, ObjectiveProcessor.class, ItemProcessor.class,
                ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(QuestCancelerIdentifierFactory.class, CancelerProcessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ActionProcessor actionProcessor = getDependency(ActionProcessor.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final ObjectiveProcessor objectiveProcessor = getDependency(ObjectiveProcessor.class);
        final ItemProcessor itemProcessor = getDependency(ItemProcessor.class);
        final Translations translations = getDependency(Translations.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);

        final QuestCancelerIdentifierFactory questCancelerIdentifierFactory = new QuestCancelerIdentifierFactory(packManager);
        identifiers.register(QuestCancelerIdentifier.class, questCancelerIdentifierFactory);
        final CancelerProcessor cancelerProcessor = new CancelerProcessor(loggerFactory.create(CancelerProcessor.class),
                loggerFactory, translations, instructions, actionProcessor, conditionProcessor,
                objectiveProcessor, itemProcessor, parsedSectionTextCreator, playerDataStorage, questCancelerIdentifierFactory);

        dependencyProvider.take(QuestCancelerIdentifierFactory.class, questCancelerIdentifierFactory);
        dependencyProvider.take(CancelerProcessor.class, cancelerProcessor);

        processorDataLoader.addProcessor(cancelerProcessor);
    }
}
