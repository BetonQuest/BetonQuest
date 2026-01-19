package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Loads and stores Journal Main Pages.
 */
public class JournalMainPageProcessor extends SectionProcessor<JournalMainPageID, JournalMainPageEntry> {

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param loggerFactory the logger factory to create new class-specific loggers
     * @param log           the custom logger for this class
     * @param placeholders  the {@link Placeholders} to create and resolve placeholders
     * @param packManager   the quest package manager to get quest packages from
     * @param textCreator   the text creator to parse text
     * @param parsers       the {@link ArgumentParsers} to use for parsing arguments
     */
    public JournalMainPageProcessor(final BetonQuestLoggerFactory loggerFactory, final BetonQuestLogger log, final Placeholders placeholders, final QuestPackageManager packManager,
                                    final ParsedSectionTextCreator textCreator, final ArgumentParsers parsers) {
        super(loggerFactory, log, placeholders, packManager, parsers, "Journal Main Page", "journal_main_page");
        this.textCreator = textCreator;
    }

    @Override
    protected Map.Entry<JournalMainPageID, JournalMainPageEntry> loadSection(final String sectionName, final SectionInstruction instruction) throws QuestException {
        final QuestPackage pack = instruction.getPackage();
        final Argument<Number> priority = instruction.read().value("priority").number().atLeast(0).get();
        final Argument<List<ConditionID>> conditions = instruction.read().value("conditions").parse(ConditionID::new).list().getOptional(Collections.emptyList());
        final Text text = textCreator.parseFromSection(pack, instruction.getSection(), "text");
        final JournalMainPageEntry pageEntry = new JournalMainPageEntry(priority.getValue(null).intValue(), conditions, text);
        return Map.entry(getIdentifier(pack, sectionName), pageEntry);
    }

    @Override
    protected JournalMainPageID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalMainPageID(packManager, pack, identifier);
    }
}
