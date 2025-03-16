package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.message.ParsedSectionMessage;
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
     * Message parser to parse messages.
     */
    private final MessageParser messageParser;

    /**
     * Player data storage to get the player language.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the variable processor to create new variables
     * @param messageParser     the message parser to parse messages
     * @param playerDataStorage the player data storage to get the player language
     */
    public JournalMainPageProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor,
                                    final MessageParser messageParser, final PlayerDataStorage playerDataStorage) {
        super(log, "Journal Main Page", "journal_main_page");
        this.variableProcessor = variableProcessor;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
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
        final ParsedSectionMessage text = new ParsedSectionMessage(variableProcessor, messageParser, playerDataStorage, pack, section, "text");
        return new JournalMainPageEntry(priority, List.copyOf(conditions), text);
    }

    @Override
    protected JournalMainPageID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalMainPageID(pack, identifier);
    }
}
