package org.betonquest.betonquest.quest.action.time;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * The time action, changing the time on the server.
 */
public class TimeAction implements NullableAction {

    /**
     * The selector to get the world for that the time should be set.
     */
    private final Argument<World> world;

    /**
     * The raw time value that will be applied.
     */
    private final Argument<TimeChange> timeChange;

    /**
     * If the rawTime needs to be transformed into Minecraft format.
     */
    private final FlagArgument<Boolean> tickFormat;

    /**
     * Creates the time action.
     *
     * @param timeChange the time type to set
     * @param world      to get the world that should be affected
     * @param tickFormat if the time needs to be multiplied with 1000
     */
    public TimeAction(final Argument<TimeChange> timeChange, final Argument<World> world, final FlagArgument<Boolean> tickFormat) {
        this.timeChange = timeChange;
        this.world = world;
        this.tickFormat = tickFormat;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final World world = this.world.getValue(profile);
        final TimeChange change = timeChange.getValue(profile);
        change.applyTo(world, !tickFormat.getValue(profile).orElse(false));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
