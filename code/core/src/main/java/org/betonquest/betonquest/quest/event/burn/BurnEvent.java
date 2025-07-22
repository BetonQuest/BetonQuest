package org.betonquest.betonquest.quest.event.burn;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * The burn event. Sets the player on fire.
 */
public class BurnEvent implements OnlineEvent {
    /**
     * Duration of the burn effect.
     */
    private final Variable<Number> duration;

    /**
     * Create a burn event that sets the player on fire for the given duration.
     *
     * @param duration duration of burn
     */
    public BurnEvent(final Variable<Number> duration) {
        this.duration = duration;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        profile.getPlayer().setFireTicks(duration.getValue(profile).intValue() * 20);
    }
}
