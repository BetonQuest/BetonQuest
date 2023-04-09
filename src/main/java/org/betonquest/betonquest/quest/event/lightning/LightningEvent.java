package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Strikes a lightning at specified location
 */
public class LightningEvent implements Event {
    /**
     * The location to strike the lightning at.
     */
    private final CompoundLocation location;
    /**
     * Whether the lightning should do damage.
     */
    private final boolean noDamage;

    /**
     * Creates a new lightning event.
     *
     * @param location the location to strike the lightning at
     * @param noDamage whether the lightning should do damage
     */
    public LightningEvent(final CompoundLocation location, final boolean noDamage) {
        this.location = location;
        this.noDamage = noDamage;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Location loc = location.getLocation(profile);
        final World world = loc.getWorld();
        if (noDamage) {
            world.strikeLightningEffect(loc);
        } else {
            world.strikeLightning(loc);
        }
    }
}
