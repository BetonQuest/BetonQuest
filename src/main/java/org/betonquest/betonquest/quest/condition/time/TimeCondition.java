package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A condition that checks the time.
 */
public class TimeCondition implements OnlineCondition {

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
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return isTimeBetween(currentTime(profile), timeMin, timeMax);
    }

    private double currentTime(final OnlineProfile profile) {
        double time = profile.getPlayer().getWorld().getTime();
        time += 6_000;
        return time / 1000 % 24;
    }

    private boolean isTimeBetween(final double time, final double start, final double end) {
        if (start <= end) {
            return time >= start && time <= end;
        } else {
            return time >= start || time <= end;
        }
    }
}
