package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.betonquest.betonquest.quest.objective.location.AbstractLocationObjective;
import org.bukkit.Location;

/**
 * This {@link AbstractLocationObjective} is completed when a player is inside a certain location while riding a train.
 */
public class TrainCartsLocationObjective extends AbstractLocationObjective {

    /**
     * The {@link Argument} that stores the location the player has to be inside.
     */
    private final Argument<Location> loc;

    /**
     * The range around the location.
     */
    private final Argument<Number> range;

    /**
     * Creates a new {@link TrainCartsLocationObjective}.
     *
     * @param service the objective service
     * @param loc     the location the player has to be inside
     * @param range   the range around the location
     * @param entry   the entry flag for this objective
     * @param exit    the exit flag for this objective
     * @throws QuestException if there is an error while parsing the instruction
     */
    public TrainCartsLocationObjective(final ObjectiveService service, final Argument<Location> loc, final Argument<Number> range,
                                       final FlagArgument<Boolean> entry, final FlagArgument<Boolean> exit) throws QuestException {
        super(service, entry, exit);
        this.loc = loc;
        this.range = range;
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
