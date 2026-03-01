package org.betonquest.betonquest.quest.action.notify;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.quest.action.CallPlayerlessActionAdapter;
import org.betonquest.betonquest.quest.action.OnlineProfileGroupPlayerlessActionAdapter;

/**
 * Factory for the notify all action.
 */
public class NotifyAllActionFactory extends NotifyActionFactory implements PlayerlessActionFactory {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates the notify all action factory.
     *
     * @param textParser       the text parser to use for parsing text
     * @param dataStorage      the storage providing player data
     * @param profileProvider  the profile provider instance
     * @param languageProvider the language provider to get the default language
     */
    public NotifyAllActionFactory(final TextParser textParser, final PlayerDataStorage dataStorage,
                                  final ProfileProvider profileProvider, final LanguageProvider languageProvider) {
        super(textParser, dataStorage, languageProvider);
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new CallPlayerlessActionAdapter(parsePlayerless(instruction));
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return new OnlineProfileGroupPlayerlessActionAdapter(profileProvider::getOnlineProfiles, super.parsePlayer(instruction));
    }
}
