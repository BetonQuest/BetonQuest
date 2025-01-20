package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory for {@link LanguageCondition}s.
 */
public class LanguageConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for required player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create a language condition factory.
     *
     * @param dataStorage the storage for used player data
     */
    public LanguageConditionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String[] languages = instruction.getArray();
        for (final String language : languages) {
            if (!Config.getLanguages().contains(language)) {
                throw new QuestException("Language " + language + " does not exist.");
            }
        }
        final Set<String> expectedLanguages = new HashSet<>(Arrays.asList(languages));
        return new LanguageCondition(dataStorage, expectedLanguages);
    }
}
