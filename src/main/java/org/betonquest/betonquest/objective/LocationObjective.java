package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

/**
 * Player has to reach certain radius around the specified location
 */
public class LocationObjective extends AbstractLocationObjective {
    /**
     * The key for the location property
     */
    private static final String LOCATION_PROPERTY = "location";

    /**
     * The location to reach
     */
    private final VariableLocation loc;

    /**
     * The range around the location
     */
    private final VariableNumber range;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @throws QuestException if there is an error while parsing the instruction
     */
    public LocationObjective(final Instruction instruction) throws QuestException {
        super(BetonQuest.getInstance().getLoggerFactory().create(LocationObjective.class), instruction);
        loc = instruction.get(VariableLocation::new);
        range = instruction.get(VariableNumber::new);
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
    public String getProperty(final String name, final Profile profile) {
        if (LOCATION_PROPERTY.equalsIgnoreCase(name)) {
            final Location location;
            try {
                location = loc.getValue(profile);
            } catch (final QuestException e) {
                log.warn(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }
}
