package org.betonquest.betonquest.quest.action.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;

/**
 * The burn action. Sets the player on fire.
 */
public class BurnAction implements OnlineAction {

    /**
     * Duration of the burn effect.
     */
    private final Argument<Number> duration;

    /**
     * The unit of the duration.
     */
    private final Argument<TimeUnit> unit;

    /**
     * Create a burn action that sets the player on fire for the given duration.
     *
     * @param duration duration of burn
     * @param unit     unit of duration
     */
    public BurnAction(final Argument<Number> duration, final Argument<TimeUnit> unit) {
        this.duration = duration;
        this.unit = unit;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final TimeUnit timeUnit = unit.getValue(profile);
        profile.getPlayer().setFireTicks((int) timeUnit.getTicks(duration.getValue(profile).intValue()));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
