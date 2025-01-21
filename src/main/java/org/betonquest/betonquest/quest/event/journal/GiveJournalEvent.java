package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
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
     * Creates a new GiveJournalEvent.
     *
     * @param playerDataSource source for the player data
     */
    public GiveJournalEvent(final Function<OnlineProfile, PlayerData> playerDataSource) {
        this.playerDataSource = playerDataSource;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        playerDataSource.apply(profile).getJournal().addToInv();
    }
}
