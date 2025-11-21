package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Loads and stores Journal entries.
 */
public class JournalEntryProcessor extends QuestProcessor<JournalEntryID, Text> {
    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store and execute journal entry logic.
     *
     * @param log         the custom logger for this class
     * @param packManager the quest package manager to get quest packages from
     * @param textCreator the text creator to parse text
     */
    public JournalEntryProcessor(final BetonQuestLogger log, final QuestPackageManager packManager,
                                 final ParsedSectionTextCreator textCreator) {
        super(log, packManager, "Journal Entry", "journal");
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

    @Override
    protected JournalEntryID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalEntryID(packManager, pack, identifier);
    }

    /**
     * Get the loaded {@link Text}s by their ID.
     *
     * @return loaded values map, reflecting changes
     */
    public Map<JournalEntryID, Text> getValues() {
        return values;
    }

    /**
     * Renames the journal entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameJournalEntry(final JournalEntryID name, final JournalEntryID rename) {
        final Text text = values.remove(name);
        values.put(rename, text);
    }
}
