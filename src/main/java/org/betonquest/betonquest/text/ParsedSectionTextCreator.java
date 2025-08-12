package org.betonquest.betonquest.text;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads value(s) from a key in a section, potentially identified by a language key.
 * When there is no section, the value will be identified by the default language.
 */
public class ParsedSectionTextCreator {

    /**
     * The text parser.
     */
    protected final TextParser textParser;

    /**
     * Storage for Player Data to retrieve the language from a profile.
     */
    protected final PlayerDataStorage playerDataStorage;

    /**
     * Language Provider to retrieve the default language.
     */
    protected final LanguageProvider languageProvider;

    /**
     * The Variable Processor to create and resolve variables.
     */
    protected final VariableProcessor variableProcessor;

    /**
     * Creates a new text Factory.
     *
     * @param variableProcessor the variable processor to create new variables
     * @param textParser        the text parser to parse the text
     * @param playerDataStorage the player data storage to get the player's language
     * @param languageProvider  the language provider to get the default language
     */
    public ParsedSectionTextCreator(final TextParser textParser, final PlayerDataStorage playerDataStorage,
                                    final LanguageProvider languageProvider, final VariableProcessor variableProcessor) {
        this.textParser = textParser;
        this.playerDataStorage = playerDataStorage;
        this.languageProvider = languageProvider;
        this.variableProcessor = variableProcessor;
    }

    /**
     * Loads value(s) from a key in a section, potentially identified by a language key.
     * When there is no section, the value will be identified by the default language.
     *
     * @param pack    the pack to resolve variables
     * @param section the section to load from
     * @param path    where the value(s) are stored in the section
     * @return the newly created text
     * @throws QuestException if there is no value, the default language is missing or the section format is invalid
     */
    public Text parseFromSection(final QuestPackage pack, final ConfigurationSection section, final String path)
            throws QuestException {
        return new ParsedSectionText(variableProcessor, textParser, playerDataStorage,
                pack, section, path, languageProvider);
    }
}
