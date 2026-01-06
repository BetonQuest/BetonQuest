package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.event.CallPlayerlessEventAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupPlayerlessEventAdapter;

/**
 * Factory for the notify all event.
 */
public class NotifyAllEventFactory extends NotifyEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates the notify all event factory.
     *
     * @param loggerFactory    the logger factory to create a logger for the events
     * @param textParser       the text parser to use for parsing text
     * @param dataStorage      the storage providing player data
     * @param profileProvider  the profile provider instance
     * @param languageProvider the language provider to get the default language
     */
    public NotifyAllEventFactory(final BetonQuestLoggerFactory loggerFactory,
                                 final TextParser textParser, final PlayerDataStorage dataStorage,
                                 final ProfileProvider profileProvider, final LanguageProvider languageProvider) {
        super(loggerFactory, textParser, dataStorage, languageProvider);
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new CallPlayerlessEventAdapter(parsePlayerless(instruction));
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return new OnlineProfileGroupPlayerlessEventAdapter(profileProvider::getOnlineProfiles, super.parsePlayer(instruction));
    }
}
