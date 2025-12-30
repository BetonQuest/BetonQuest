package org.betonquest.betonquest.quest.condition.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Requires the player to be in specified distance from a location.
 */
public class LocationCondition implements OnlineCondition {

    /**
     * The location.
     */
    private final Argument<Location> loc;

    /**
     * The range around the location.
     */
    private final Argument<Number> range;

    /**
     * Creates a new location condition.
     *
     * @param loc   the location
     * @param range the range around the location
     */
    public LocationCondition(final Argument<Location> loc, final Argument<Number> range) {
        this.loc = loc;
        this.range = range;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Location location = loc.getValue(profile);
        final Player player = profile.getPlayer();
        if (!location.getWorld().equals(player.getWorld())) {
            return false;
        }
        final double pRange = range.getValue(profile).doubleValue();
        return player.getLocation().distanceSquared(location) <= pRange * pRange;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
