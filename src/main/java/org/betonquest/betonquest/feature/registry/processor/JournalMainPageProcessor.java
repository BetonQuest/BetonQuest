package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads and stores Journal Main Pages.
 */
public class JournalMainPageProcessor extends SectionProcessor<JournalMainPageID, JournalMainPageEntry> {

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the variable processor to create new variables
     */
    public JournalMainPageProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        super(log, "Journal Main Page", "journal_main_page");
        this.variableProcessor = variableProcessor;
    }

    @Override
    protected JournalMainPageEntry loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final int priority = section.getInt("priority", -1);
        if (priority < 0) {
            throw new QuestException("Priority of journal main page needs to be at least 0!");
        }
        final String rawConditions = GlobalVariableResolver.resolve(pack, section.getString("conditions"));
        final List<ConditionID> conditions;
        if (rawConditions == null || rawConditions.isEmpty()) {
            conditions = List.of();
        } else {
            final String[] split = rawConditions.split(",");
            conditions = new ArrayList<>(split.length);
            for (final String conditionString : split) {
                if (!conditionString.isEmpty()) {
                    conditions.add(new ConditionID(pack, conditionString));
                }
            }
        }
        final ParsedSectionMessage text = new ParsedSectionMessage(variableProcessor, pack, section, "text");
        return new JournalMainPageEntry(priority, List.copyOf(conditions), text);
    }

    @Override
    protected JournalMainPageID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalMainPageID(pack, identifier);
    }
}
