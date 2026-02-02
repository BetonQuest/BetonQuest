package org.betonquest.betonquest.quest.action.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * The burn action. Sets the player on fire.
 */
public class BurnAction implements OnlineAction {

    /**
     * Duration of the burn effect.
     */
    private final Argument<Number> duration;

    /**
     * Create a burn action that sets the player on fire for the given duration.
     *
     * @param duration duration of burn
     */
    public BurnAction(final Argument<Number> duration) {
        this.duration = duration;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        profile.getPlayer().setFireTicks(duration.getValue(profile).intValue() * 20);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
