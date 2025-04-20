package org.betonquest.betonquest.quest.condition.language;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.HashSet;
import java.util.List;
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
        final List<String> languages = instruction.getList();
        for (final String language : languages) {
            if (!pluginMessage.getLanguages().contains(language)) {
                throw new QuestException("Language " + language + " does not exist.");
            }
        }
        final Set<String> expectedLanguages = new HashSet<>(languages);
        return new LanguageCondition(dataStorage, languageProvider, expectedLanguages);
    }
}
