package org.betonquest.betonquest.quest.condition.time;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableWorld;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * A condition that checks the time.
 */
public class TimeCondition implements NullableCondition {

    /**
     * The time frame in which the time should be.
     */
    private final TimeFrame timeFrame;

    /**
     * The variable world.
     */
    private final VariableWorld variableWorld;

    /**
     * Creates a new TimeCondition.
     *
     * @param timeFrame     the time frame
     * @param variableWorld the variable world
     */
    public TimeCondition(final TimeFrame timeFrame, final VariableWorld variableWorld) {
        this.timeFrame = timeFrame;
        this.variableWorld = variableWorld;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        return isTimeBetween(currentTime(variableWorld.getValue(profile)), timeFrame.start(), timeFrame.end());
    }

    private double currentTime(final World world) {
        double time = world.getTime();
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
