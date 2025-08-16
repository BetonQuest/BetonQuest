package org.betonquest.betonquest.quest.objective.location;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

/**
 * Player has to reach certain radius around the specified location.
 */
public class LocationObjective extends AbstractLocationObjective {
    /**
     * The key for the location property.
     */
    private static final String LOCATION_PROPERTY = "location";

    /**
     * The location to reach.
     */
    private final Variable<Location> loc;

    /**
     * The range around the location.
     */
    private final Variable<Number> range;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @param loc         the VariableLocation object representing the location
     * @param range       the VariableNumber object representing the range
     * @throws QuestException if there is an error while parsing the instruction
     */
    public LocationObjective(final Instruction instruction, final Variable<Location> loc, final Variable<Number> range) throws QuestException {
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
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
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
