package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.Set;

/**
 * A condition that checks if the player has selected a specific language.
 */
public class LanguageCondition implements PlayerCondition {

    /**
     * Storage for used player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Set of languages that the player must have selected for the condition to be true.
     */
    private final Set<String> expectedLanguages;

    /**
     * Create a language condition.
     *
     * @param dataStorage       the stored for the required player data
     * @param expectedLanguages the languages that the player must have selected
     */
    public LanguageCondition(final PlayerDataStorage dataStorage, final Set<String> expectedLanguages) {
        this.dataStorage = dataStorage;
        this.expectedLanguages = expectedLanguages;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String playerLanguage = dataStorage.getOffline(profile).getLanguage();
        return expectedLanguages.contains(playerLanguage);
    }
}
