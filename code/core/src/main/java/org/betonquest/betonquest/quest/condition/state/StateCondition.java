package org.betonquest.betonquest.quest.condition.state;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;

/**
 * A condition that checks if a player is in a certain state.
 */
public class StateCondition implements OnlineCondition {

    /**
     * The state to check for.
     */
    private final Argument<PlayerStates> playerState;

    /**
     * Creates a new state condition.
     *
     * @param playerState the state to check for
     */
    public StateCondition(final Argument<PlayerStates> playerState) {
        this.playerState = playerState;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return playerState.getValue(profile).checkState(profile.getPlayer());
    }
}
