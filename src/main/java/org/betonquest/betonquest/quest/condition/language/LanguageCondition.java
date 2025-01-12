package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Set;

/**
 * A condition that checks if the player has selected a specific language.
 */
public class LanguageCondition implements PlayerCondition {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Set of languages that the player must have selected for the condition to be true.
     */
    private final Set<String> expectedLanguages;

    /**
     * Create a language condition.
     *
     * @param betonQuest        the BetonQuest instance
     * @param expectedLanguages the languages that the player must have selected
     */
    public LanguageCondition(final BetonQuest betonQuest, final Set<String> expectedLanguages) {
        this.betonQuest = betonQuest;
        this.expectedLanguages = expectedLanguages;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String playerLanguage = betonQuest.getOfflinePlayerData(profile).getLanguage();
        return expectedLanguages.contains(playerLanguage);
    }
}
