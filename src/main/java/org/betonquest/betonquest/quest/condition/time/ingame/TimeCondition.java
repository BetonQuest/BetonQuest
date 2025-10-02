package org.betonquest.betonquest.quest.condition.time.ingame;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.quest.condition.time.Time;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * A condition that checks the time.
 */
public class TimeCondition implements NullableCondition {

    /**
     * The time frame in which the time should be.
     */
    private final Variable<TimeFrame> timeFrame;

    /**
     * The variable world.
     */
    private final Variable<World> variableWorld;

    /**
     * Creates a new TimeCondition.
     *
     * @param timeFrame     the time frame
     * @param variableWorld the variable world
     */
    public TimeCondition(final Variable<TimeFrame> timeFrame, final Variable<World> variableWorld) {
        this.timeFrame = timeFrame;
        this.variableWorld = variableWorld;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return timeFrame.getValue(profile).isTimeBetween(currentTime(variableWorld.getValue(profile)));
    }

    private Time currentTime(final World world) {
        final long time = world.getTime() + 6_000;
        final int hours = (int) (time / 1_000) % 24;
        final int minutes = (int) (time % 1000) * 60 / 1000;
        return new Time(hours, minutes);
    }
}
