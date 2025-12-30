package org.betonquest.betonquest.quest.event.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

/**
 * The burn event. Sets the player on fire.
 */
public class BurnEvent implements OnlineEvent {

    /**
     * The duration of the burn effect.
     */
    private final Argument<Number> duration;

    /**
     * Create a burn event that sets the player on fire for the given duration.
     *
     * @param duration the duration of the burn effect
     */
    public BurnEvent(final Argument<Number> duration) {
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
