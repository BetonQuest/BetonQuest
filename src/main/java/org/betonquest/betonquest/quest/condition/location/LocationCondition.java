package org.betonquest.betonquest.quest.condition.location;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Requires the player to be in specified distance from a location.
 */
public class LocationCondition implements OnlineCondition {

    /**
     * The location.
     */
    private final VariableLocation loc;

    /**
     * The range around the location.
     */
    private final VariableNumber range;

    /**
     * Creates a new location condition.
     *
     * @param loc   The location
     * @param range The range around the location
     */
    public LocationCondition(final VariableLocation loc, final VariableNumber range) {
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
