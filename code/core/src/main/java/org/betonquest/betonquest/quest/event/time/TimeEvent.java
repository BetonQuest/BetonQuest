package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.Selector;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * The time event, changing the time on the server.
 */
public class TimeEvent implements NullableEvent {

    /**
     * The selector to get the world for that the time should be set.
     */
    private final Selector<World> worldSelector;

    /**
     * The raw time value that will be applied.
     */
    private final Argument<TimeChange> timeChange;

    /**
     * If the rawTime needs to be transformed into Minecraft format.
     */
    private final boolean hourFormat;

    /**
     * Creates the time event.
     *
     * @param timeChange    the time type to set
     * @param worldSelector to get the world that should be affected
     * @param hourFormat    if the time needs to be multiplied with 1000
     */
    public TimeEvent(final Argument<TimeChange> timeChange, final Selector<World> worldSelector, final boolean hourFormat) {
        this.timeChange = timeChange;
        this.worldSelector = worldSelector;
        this.hourFormat = hourFormat;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final World world = worldSelector.selectFor(profile);
        final TimeChange change = timeChange.getValue(profile);
        change.applyTo(world, hourFormat);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
