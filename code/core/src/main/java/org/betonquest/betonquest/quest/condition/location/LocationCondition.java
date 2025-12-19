package org.betonquest.betonquest.quest.condition.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
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
    private final Variable<Location> loc;

    /**
     * The range around the location.
     */
    private final Variable<Number> range;

    /**
     * Creates a new location condition.
     *
     * @param loc   The location
     * @param range The range around the location
     */
    public LocationCondition(final Variable<Location> loc, final Variable<Number> range) {
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
}
