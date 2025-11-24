package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;

import java.util.List;

/**
 * Factory to create Journal objects for profiles.
 */
public class JournalFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * A {@link ConfigAccessor} that contains the journal's configuration.
     */
    private final ConfigAccessor config;

    /**
     * The message parser to use for parsing messages.
     */
    private final TextParser textParser;

    /**
     * The font registry used to get the width of the characters.
     */
    private final FontRegistry fontRegistry;

    /**
     * Create a new Factory for Journals.
     *
     * @param loggerFactory the logger Factory to create new class specific logger
     * @param pluginMessage the {@link PluginMessage} instance
     * @param questTypeApi  the Quest Type API
     * @param featureApi    the Feature API
     * @param config        a {@link ConfigAccessor} that contains the journal's configuration
     * @param textParser    the text parser to use for parsing text
     * @param fontRegistry  the font registry to get the width of the characters
     */
    public JournalFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage,
                          final QuestTypeApi questTypeApi, final FeatureApi featureApi,
                          final ConfigAccessor config, final TextParser textParser, final FontRegistry fontRegistry) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.config = config;
        this.textParser = textParser;
        this.fontRegistry = fontRegistry;
    }

    /**
     * Create a new Journal.
     *
     * @param profile  the profile to create the journal for
     * @param pointers the active journal pointers
     * @return the newly created journal
     */
    public Journal createJournal(final Profile profile, final List<Pointer> pointers) {
        final BetonQuestLogger log = loggerFactory.create(Journal.class);
        return new Journal(log, pluginMessage, questTypeApi, featureApi, textParser, fontRegistry, profile, pointers, config);
    }
}
