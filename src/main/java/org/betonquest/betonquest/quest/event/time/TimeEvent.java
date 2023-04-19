package org.betonquest.betonquest.quest.event.time;

import org.betonquest.betonquest.api.common.worldselector.WorldSelector;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.World;

/**
 * The time event, changing the time on the server.
 */
public class TimeEvent implements Event {
    /**
     * The type of time that will be applied.
     */
    private final Time time;

    /**
     * The selector to get the world for that the time should be set.
     */
    private final WorldSelector worldSelector;

    /**
     * The raw time value that will be applied.
     */
    private final long rawTime;

    /**
     * Create the time event.
     *
     * @param time          the time type to set
     * @param rawTime       the raw time value to set
     * @param worldSelector to get the world that should be affected
     */
    public TimeEvent(final Time time, final long rawTime, final WorldSelector worldSelector) {
        this.time = time;
        this.rawTime = rawTime;
        this.worldSelector = worldSelector;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final World world = worldSelector.getWorld(profile);
        world.setTime(time.applyTo(world, rawTime));
    }
}
