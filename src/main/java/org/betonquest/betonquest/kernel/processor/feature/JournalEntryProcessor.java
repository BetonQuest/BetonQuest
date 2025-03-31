package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.kernel.processor.QuestProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessage;
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
     * Message parser to parse messages.
     */
    private final MessageParser messageParser;

    /**
     * Player data storage to get the player language.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Create a new QuestProcessor to store and execute journal entry logic.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the processor to create new variables
     * @param messageParser     the message parser to parse messages
     * @param playerDataStorage the player data storage to get the player language
     * @param languageProvider  the language provider to get the default language
     */
    public JournalEntryProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor,
                                 final MessageParser messageParser, final PlayerDataStorage playerDataStorage,
                                 final LanguageProvider languageProvider) {
        super(log, "Journal Entry", "journal");
        this.variableProcessor = variableProcessor;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
        this.languageProvider = languageProvider;
    }

    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(internal);
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            try {
                values.put(getIdentifier(pack, key), new ParsedSectionMessage(variableProcessor, messageParser, playerDataStorage, pack, section, key, languageProvider));
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
