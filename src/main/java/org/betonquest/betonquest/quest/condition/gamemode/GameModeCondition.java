package org.betonquest.betonquest.quest.condition.gamemode;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.GameMode;

/**
 * A condition that checks if the player is in a specific game mode.
 */
public class GameModeCondition implements OnlineCondition {

    /**
     * The game mode to check for.
     */
    private final GameMode gameMode;

    /**
     * Creates a new game mode condition.
     *
     * @param gameMode The game mode to check for.
     */
    public GameModeCondition(final GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getGameMode() == gameMode;
    }
}
