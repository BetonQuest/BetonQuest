package org.betonquest.betonquest.quest.condition.time.ingame;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
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
    private final Argument<TimeFrame> timeFrame;

    /**
     * The variable world.
     */
    private final Argument<World> world;

    /**
     * Creates a new TimeCondition.
     *
     * @param timeFrame the time frame
     * @param world     the variable world
     */
    public TimeCondition(final Argument<TimeFrame> timeFrame, final Argument<World> world) {
        this.timeFrame = timeFrame;
        this.world = world;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        return timeFrame.getValue(profile).isTimeBetween(currentTime(world.getValue(profile)));
    }

    private Time currentTime(final World world) {
        final long time = world.getTime() + 6_000;
        final int hours = (int) (time / 1_000) % 24;
        final int minutes = (int) (time % 1000) * 60 / 1000;
        return new Time(hours, minutes);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
