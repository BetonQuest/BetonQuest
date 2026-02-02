package org.betonquest.betonquest.quest.action.journal;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.database.PlayerData;

import java.util.function.Function;

/**
 * Gives journal to the player.
 */
public class GiveJournalAction implements OnlineAction {

    /**
     * Function to get the player data for a given online profile.
     */
    private final Function<OnlineProfile, PlayerData> playerDataSource;

    /**
     * Creates a new GiveJournalAction.
     *
     * @param playerDataSource source for the player data
     */
    public GiveJournalAction(final Function<OnlineProfile, PlayerData> playerDataSource) {
        this.playerDataSource = playerDataSource;
    }

    @Override
    public void execute(final OnlineProfile profile) {
        playerDataSource.apply(profile).getJournal().addToInv();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
