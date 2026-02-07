package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Loads and stores Journal entries.
 */
public class JournalEntryProcessor extends QuestProcessor<JournalEntryIdentifier, Text> {

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store and execute journal entry logic.
     *
     * @param log                           the custom logger for this class
     * @param journalEntryIdentifierFactory the identifier factory to create {@link JournalEntryIdentifier}s for this type
     * @param textCreator                   the text creator to parse text
     */
    public JournalEntryProcessor(final BetonQuestLogger log,
                                 final IdentifierFactory<JournalEntryIdentifier> journalEntryIdentifierFactory, final ParsedSectionTextCreator textCreator) {
        super(log, journalEntryIdentifierFactory, "Journal Entry", "journal");
        this.textCreator = textCreator;
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(internal);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            try {
                values.put(getIdentifier(pack, key), textCreator.parseFromSection(pack, section, key));
            } catch (final QuestException e) {
                log.warn("Could not load " + readable + " '" + key + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    /**
     * Get the loaded {@link Text}s by their ID.
     *
     * @return loaded values map, reflecting changes
     */
    public Map<JournalEntryIdentifier, Text> getValues() {
        return values;
    }

    /**
     * Renames the journal entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameJournalEntry(final JournalEntryIdentifier name, final JournalEntryIdentifier rename) {
        final Text text = values.remove(name);
        values.put(rename, text);
    }
}
