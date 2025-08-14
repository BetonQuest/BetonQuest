package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.PlayerData;

import java.util.function.Function;

/**
 * Gives journal to the player.
 */
public class GiveJournalEvent implements OnlineEvent {
    /**
     * Function to get the player data for a given online profile.
     */
    private final Function<OnlineProfile, PlayerData> playerDataSource;

    /**
     * Plugin Message instance to create the journal.
     */
    private final PluginMessage pluginMessage;

    /**
     * The text parser used to parse text.
     */
    private final TextParser textParser;

    /**
     * Creates a new GiveJournalEvent.
     *
     * @param playerDataSource source for the player data
     * @param pluginMessage    the plugin message to create the journal
     * @param textParser       the text parser used to parse text
     */
    public GiveJournalEvent(final Function<OnlineProfile, PlayerData> playerDataSource, final PluginMessage pluginMessage,
                            final TextParser textParser) {
        this.playerDataSource = playerDataSource;
        this.pluginMessage = pluginMessage;
        this.textParser = textParser;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        playerDataSource.apply(profile).getJournal(pluginMessage, textParser).addToInv();
    }
}
