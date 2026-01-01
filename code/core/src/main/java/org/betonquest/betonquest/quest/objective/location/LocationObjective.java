package org.betonquest.betonquest.quest.objective.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;

/**
 * Player has to reach a certain radius around the specified location.
 */
public class LocationObjective extends AbstractLocationObjective {

    /**
     * The key for the location property.
     */
    private static final String LOCATION_PROPERTY = "location";

    /**
     * The location to reach.
     */
    private final Argument<Location> loc;

    /**
     * The range around the location.
     */
    private final Argument<Number> range;

    /**
     * The constructor takes an Instruction object as a parameter and throws a QuestException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @param loc         the target location
     * @param range       the radius defining the area surrounding the target location
     * @throws QuestException if there is an error while parsing the instruction
     */
    public LocationObjective(final Instruction instruction, final Argument<Location> loc, final Argument<Number> range) throws QuestException {
        super(instruction);
        this.loc = loc;
        this.range = range;
    }

    @Override
    protected boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestException {
        final Location targetLocation = loc.getValue(onlineProfile);
        if (!location.getWorld().equals(targetLocation.getWorld())) {
            return false;
        }
        final double pRange = range.getValue(onlineProfile).doubleValue();
        return location.distanceSquared(targetLocation) <= pRange * pRange;
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        if (LOCATION_PROPERTY.equalsIgnoreCase(name)) {
            final Location location = loc.getValue(profile);
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }
}
