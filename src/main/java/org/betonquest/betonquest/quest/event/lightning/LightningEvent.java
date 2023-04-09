package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;

/**
 * Strikes a lightning at specified location
 */
public class LightningEvent implements Event {
    /**
     * The location to strike the lightning at.
     */
    private final CompoundLocation location;

    /**
     * Creates a new lightning event.
     *
     * @param location the location to strike the lightning at
     */
    public LightningEvent(final CompoundLocation location) {
        this.location = location;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Location loc = location.getLocation(profile);
        loc.getWorld().strikeLightning(loc);
    }
}
