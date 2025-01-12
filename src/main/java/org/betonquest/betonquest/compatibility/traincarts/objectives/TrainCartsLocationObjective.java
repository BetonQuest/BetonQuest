package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.objectives.AbstractLocationObjective;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * This {@link AbstractLocationObjective} is completed when a player is inside a certain location while riding a train.
 */
public class TrainCartsLocationObjective extends AbstractLocationObjective implements Listener {
    /**
     * The {@link VariableLocation} that stores the location the player has to be inside.
     */
    private final VariableLocation loc;

    /**
     * The {@link VariableNumber} that stores the range around the location.
     */
    private final VariableNumber range;

    /**
     * Creates a new {@link TrainCartsLocationObjective}.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @throws QuestException if there is an error while parsing the instruction
     */
    public TrainCartsLocationObjective(final Instruction instruction) throws QuestException {
        super(BetonQuest.getInstance().getLoggerFactory().create(TrainCartsLocationObjective.class), instruction);
        this.loc = instruction.getLocation();
        this.range = instruction.getVarNum(instruction.getOptional("range", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
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
        return "";
    }

    @Override
    protected boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestException {
        if (!TrainCartsUtils.isRidingTrainCart(onlineProfile)) {
            return false;
        }

        final Location targetLocation = loc.getValue(onlineProfile);
        return targetLocation.getWorld().equals(location.getWorld()) && location.distanceSquared(targetLocation) <= range.getValue(onlineProfile).doubleValue();
    }
}
