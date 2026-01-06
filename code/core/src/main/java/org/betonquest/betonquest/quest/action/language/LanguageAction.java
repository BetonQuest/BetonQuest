package org.betonquest.betonquest.quest.action.language;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Changes player's language.
 */
public class LanguageAction implements PlayerAction {

    /**
     * The language to set.
     */
    private final Argument<String> language;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the language event.
     *
     * @param language    the language to set
     * @param dataStorage the storage providing player data
     */
    public LanguageAction(final Argument<String> language, final PlayerDataStorage dataStorage) {
        this.language = language;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final String lang = language.getValue(profile);
        dataStorage.getOffline(profile).setLanguage("default".equalsIgnoreCase(lang) ? null : lang);
    }
}
