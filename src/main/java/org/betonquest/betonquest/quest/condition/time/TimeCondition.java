package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A condition that checks the time.
 */
public class TimeCondition implements PlayerCondition {

    /**
     * The minimum time.
     */
    private final double timeMin;

    /**
     * The maximum time.
     */
    private final double timeMax;

    /**
     * Creates a new TimeCondition.
     *
     * @param timeMin the minimum time
     * @param timeMax the maximum time
     */
    public TimeCondition(final double timeMin, final double timeMax) {
        this.timeMin = timeMin;
        this.timeMax = timeMax;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        return isTimeBetween(currentTime(profile), timeMin, timeMax);
    }

    private double currentTime(final Profile profile) {
        double time = profile.getOnlineProfile().get().getPlayer().getWorld().getTime();
        final int midnight = 18_000;
        if (time >= midnight) {
            time = time / 1000 - 18;
        } else {
            time = time / 1000 + 6;
        }
        return time;
    }

    private boolean isTimeBetween(final double time, final double start, final double end) {
        if (start <= end) {
            return time >= start && time <= end;
        } else {
            return time >= start || time <= end;
        }
    }
}
