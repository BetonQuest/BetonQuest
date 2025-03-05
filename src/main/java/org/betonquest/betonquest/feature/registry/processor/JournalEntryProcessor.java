package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.betonquest.betonquest.quest.registry.processor.QuestProcessor;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Loads and stores Journal entries.
 */
public class JournalEntryProcessor extends QuestProcessor<JournalEntryID, ParsedSectionMessage> {

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new QuestProcessor to store and execute journal entry logic.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the processor to create new variables
     */
    public JournalEntryProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        super(log, "Journal Entry", "journal");
        this.variableProcessor = variableProcessor;
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(internal);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            try {
                values.put(getIdentifier(pack, key), new ParsedSectionMessage(variableProcessor, pack, section, key));
            } catch (final QuestException e) {
                log.warn("Could not load " + readable + " '" + key + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected JournalEntryID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalEntryID(pack, identifier);
    }

    /**
     * Get the loaded {@link ParsedSectionMessage}s by their ID.
     *
     * @return loaded values map, reflecting changes
     */
    public Map<JournalEntryID, ParsedSectionMessage> getValues() {
        return values;
    }

    /**
     * Renames the journal entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameJournalEntry(final JournalEntryID name, final JournalEntryID rename) {
        final ParsedSectionMessage message = values.remove(name);
        values.put(rename, message);
    }
}
