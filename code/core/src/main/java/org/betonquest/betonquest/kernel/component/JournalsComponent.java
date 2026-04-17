package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.betonquest.betonquest.id.journal.JournalEntryIdentifierFactory;
import org.betonquest.betonquest.id.journal.JournalMainPageIdentifierFactory;
import org.betonquest.betonquest.kernel.ProcessorDataLoader;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent}
 * for {@link JournalEntryProcessor}, {@link JournalMainPageProcessor} and {@link JournalFactory}.
 */
public class JournalsComponent extends AbstractCoreComponent {

    /**
     * Create a new JournalsComponent.
     */
    public JournalsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(QuestPackageManager.class, BetonQuestLoggerFactory.class, ConfigAccessor.class,
                Identifiers.class, Instructions.class, Localizations.class, TextParser.class,
                ParsedSectionTextCreator.class, FontRegistry.class, ConditionManager.class, ProcessorDataLoader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(JournalEntryIdentifierFactory.class, JournalMainPageIdentifierFactory.class,
                JournalEntryProcessor.class, JournalMainPageProcessor.class, JournalFactory.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ParsedSectionTextCreator parsedSectionTextCreator = getDependency(ParsedSectionTextCreator.class);
        final Localizations localizations = getDependency(Localizations.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final TextParser textParser = getDependency(TextParser.class);
        final FontRegistry fontRegistry = getDependency(FontRegistry.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ProcessorDataLoader processorDataLoader = getDependency(ProcessorDataLoader.class);
        final DatabaseComponent databaseComponent = getDependency(DatabaseComponent.class);

        final JournalEntryIdentifierFactory journalEntryIdentifierFactory = new JournalEntryIdentifierFactory(questPackageManager);
        identifiers.register(JournalEntryIdentifier.class, journalEntryIdentifierFactory);
        final JournalEntryProcessor journalEntryProcessor = new JournalEntryProcessor(loggerFactory.create(JournalEntryProcessor.class),
                journalEntryIdentifierFactory, parsedSectionTextCreator);

        final JournalMainPageIdentifierFactory journalMainPageIdentifierFactory = new JournalMainPageIdentifierFactory(questPackageManager);
        identifiers.register(JournalMainPageIdentifier.class, journalMainPageIdentifierFactory);
        final JournalMainPageProcessor journalMainPageProcessor = new JournalMainPageProcessor(loggerFactory.create(JournalMainPageProcessor.class),
                instructions, parsedSectionTextCreator, journalMainPageIdentifierFactory);

        final JournalFactory journalFactory = new JournalFactory(loggerFactory, localizations, conditionManager,
                journalMainPageProcessor, journalEntryProcessor, config, textParser, fontRegistry, databaseComponent);

        dependencyProvider.take(JournalEntryIdentifierFactory.class, journalEntryIdentifierFactory);
        dependencyProvider.take(JournalMainPageIdentifierFactory.class, journalMainPageIdentifierFactory);
        dependencyProvider.take(JournalEntryProcessor.class, journalEntryProcessor);
        dependencyProvider.take(JournalMainPageProcessor.class, journalMainPageProcessor);
        dependencyProvider.take(JournalFactory.class, journalFactory);

        processorDataLoader.addProcessor(journalEntryProcessor);
        processorDataLoader.addProcessor(journalMainPageProcessor);
    }
}
