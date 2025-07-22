package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.CallPlayerlessEventAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupPlayerlessEventAdapter;

/**
 * Factory for the notify all event.
 */
public class NotifyAllEventFactory extends NotifyEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates the notify all event factory.
     *
     * @param loggerFactory    the logger factory to create a logger for the events
     * @param data             the data for primary server thread access
     * @param messageParser    the message parser to use for parsing messages
     * @param dataStorage      the storage providing player data
     * @param profileProvider  the profile provider instance
     * @param languageProvider the language provider to get the default language
     */
    public NotifyAllEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                                 final MessageParser messageParser, final PlayerDataStorage dataStorage,
                                 final ProfileProvider profileProvider, final LanguageProvider languageProvider) {
        super(loggerFactory, data, messageParser, dataStorage, languageProvider);
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new CallPlayerlessEventAdapter(parsePlayerless(instruction));
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new OnlineProfileGroupPlayerlessEventAdapter(profileProvider::getOnlineProfiles, super.parsePlayer(instruction));
    }
}
