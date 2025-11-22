package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.List;

/**
 * A condition that checks if the player has selected a specific language.
 */
public class LanguageCondition implements PlayerCondition {

    /**
     * Storage for used player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Set of languages that the player must have selected for the condition to be true.
     */
    private final Variable<List<String>> expectedLanguages;

    /**
     * Create a language condition.
     *
     * @param dataStorage       the stored for the required player data
     * @param languageProvider  the language provider to get the default language
     * @param expectedLanguages the languages that the player must have selected
     */
    public LanguageCondition(final PlayerDataStorage dataStorage, final LanguageProvider languageProvider,
                             final Variable<List<String>> expectedLanguages) {
        this.dataStorage = dataStorage;
        this.languageProvider = languageProvider;
        this.expectedLanguages = expectedLanguages;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String playerLanguage = dataStorage.getOffline(profile).getLanguage().orElseGet(languageProvider::getDefaultLanguage);
        return expectedLanguages.getValue(profile).contains(playerLanguage);
    }
}
