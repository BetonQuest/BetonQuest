package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.objective.location.AbstractLocationObjective;
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
     * @param loc         the location the player has to be inside
     * @param range       the range around the location
     * @throws QuestException if there is an error while parsing the instruction
     */
    public TrainCartsLocationObjective(final Instruction instruction, final VariableLocation loc, final VariableNumber range) throws QuestException {
        super(BetonQuest.getInstance().getLoggerFactory().create(TrainCartsLocationObjective.class), instruction);
        this.loc = loc;
        this.range = range;
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
