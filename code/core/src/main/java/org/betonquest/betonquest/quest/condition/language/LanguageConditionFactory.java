package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.List;

/**
 * Factory for {@link LanguageCondition}s.
 */
public class LanguageConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for required player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * The {@link Translations} instance.
     */
    private final Translations translations;

    /**
     * Create a language condition factory.
     *
     * @param dataStorage      the storage for used player data
     * @param languageProvider the language provider to get the default language
     * @param translations     the {@link Translations} instance
     */
    public LanguageConditionFactory(final PlayerDataStorage dataStorage, final LanguageProvider languageProvider,
                                    final Translations translations) {
        this.dataStorage = dataStorage;
        this.languageProvider = languageProvider;
        this.translations = translations;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<String>> languages = instruction.string().validate(
                        language -> translations.getLanguages().contains(language),
                        "Language '%s' does not exist.")
                .list().get();
        return new LanguageCondition(dataStorage, languageProvider, languages);
    }
}
