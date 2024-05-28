package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
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
    private final CompoundLocation loc;

    /**
     * The range around the location
     */
    private final VariableNumber range;

    /**
     * The constructor takes an Instruction object as a parameter and throws an InstructionParseException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @throws InstructionParseException if there is an error while parsing the instruction
     */
    public LocationObjective(final Instruction instruction) throws InstructionParseException {
        super(BetonQuest.getInstance().getLoggerFactory().create(LocationObjective.class), instruction);
        loc = instruction.getLocation();
        range = instruction.getVarNum();
    }

    @Override
    protected boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestRuntimeException {
        final Location targetLocation = loc.getLocation(onlineProfile);
        if (!location.getWorld().equals(targetLocation.getWorld())) {
            return false;
        }
        final double pRange = range.getDouble(onlineProfile);
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
                location = loc.getLocation(profile);
            } catch (final QuestRuntimeException e) {
                log.warn(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }

}
