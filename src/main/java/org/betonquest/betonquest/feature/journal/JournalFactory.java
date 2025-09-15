package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.config.PluginMessage;

import java.util.List;

/**
 * Factory to create Journal objects for profiles.
 */
public class JournalFactory {
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
     * Create a new Factory for Journals.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param questTypeApi  the Quest Type API
     * @param featureApi    the Feature API
     * @param config        a {@link ConfigAccessor} that contains the journal's configuration
     */
    public JournalFactory(final PluginMessage pluginMessage, final QuestTypeApi questTypeApi, final FeatureApi featureApi,
                          final ConfigAccessor config) {
        this.pluginMessage = pluginMessage;
        this.questTypeApi = questTypeApi;
        this.featureApi = featureApi;
        this.config = config;
    }

    /**
     * Create a new Journal.
     *
     * @param profile  the profile to create the journal for
     * @param pointers the active journal pointers
     * @return the newly created journal
     */
    public Journal createJournal(final Profile profile, final List<Pointer> pointers) {
        return new Journal(pluginMessage, questTypeApi, featureApi, profile, pointers, config);
    }
}
