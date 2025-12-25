package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.config.PluginMessage;
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
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create a language condition factory.
     *
     * @param dataStorage      the storage for used player data
     * @param languageProvider the language provider to get the default language
     * @param pluginMessage    the {@link PluginMessage} instance
     */
    public LanguageConditionFactory(final PlayerDataStorage dataStorage, final LanguageProvider languageProvider,
                                    final PluginMessage pluginMessage) {
        this.dataStorage = dataStorage;
        this.languageProvider = languageProvider;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<String>> languages = instruction.string().validate(language -> {
            if (!pluginMessage.getLanguages().contains(language)) {
                throw new QuestException("Language " + language + " does not exist.");
            }
            return true;
        }).getList();
        return new LanguageCondition(dataStorage, languageProvider, languages);
    }
}
