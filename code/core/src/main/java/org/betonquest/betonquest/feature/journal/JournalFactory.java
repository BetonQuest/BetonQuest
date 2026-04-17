package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.kernel.component.DatabaseComponent;
import org.betonquest.betonquest.kernel.processor.feature.JournalEntryProcessor;
import org.betonquest.betonquest.kernel.processor.feature.JournalMainPageProcessor;

import java.util.List;

/**
 * Factory to create Journal objects for profiles.
 */
public class JournalFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * The Condition Manager.
     */
    private final ConditionManager conditionManager;

    /**
     * The main page processor.
     */
    private final JournalMainPageProcessor mainPageProcessor;

    /**
     * The entry processor.
     */
    private final JournalEntryProcessor entryProcessor;

    /**
     * A {@link ConfigAccessor} that contains the journal's configuration.
     */
    private final ConfigAccessor config;

    /**
     * The message parser to use for parsing messages.
     */
    private final TextParser textParser;

    /**
     * The font registry used to get the width of the characters.
     */
    private final FontRegistry fontRegistry;

    /**
     * The database component.
     */
    private final DatabaseComponent databaseComponent;

    /**
     * Create a new Factory for Journals.
     *
     * @param loggerFactory     the logger Factory to create new class specific logger
     * @param localizations     the {@link Localizations} instance
     * @param conditionManager  the Condition Manager
     * @param entryProcessor    the {@link JournalEntryProcessor} to process journal entries
     * @param mainPageProcessor the {@link JournalMainPageProcessor} to process the main page
     * @param config            a {@link ConfigAccessor} that contains the journal's configuration
     * @param textParser        the text parser to use for parsing text
     * @param fontRegistry      the font registry to get the width of the characters
     * @param databaseComponent the database component
     */
    public JournalFactory(final BetonQuestLoggerFactory loggerFactory, final Localizations localizations,
                          final ConditionManager conditionManager, final JournalMainPageProcessor mainPageProcessor,
                          final JournalEntryProcessor entryProcessor, final ConfigAccessor config,
                          final TextParser textParser, final FontRegistry fontRegistry, final DatabaseComponent databaseComponent) {
        this.loggerFactory = loggerFactory;
        this.localizations = localizations;
        this.conditionManager = conditionManager;
        this.mainPageProcessor = mainPageProcessor;
        this.entryProcessor = entryProcessor;
        this.config = config;
        this.textParser = textParser;
        this.fontRegistry = fontRegistry;
        this.databaseComponent = databaseComponent;
    }

    /**
     * Create a new Journal.
     *
     * @param profile  the profile to create the journal for
     * @param pointers the active journal pointers
     * @return the newly created journal
     */
    public Journal createJournal(final Profile profile, final List<Pointer> pointers) {
        final BetonQuestLogger log = loggerFactory.create(Journal.class);
        return new Journal(log, localizations, conditionManager, mainPageProcessor, entryProcessor, textParser,
                fontRegistry, profile, pointers, config, databaseComponent);
    }
}
