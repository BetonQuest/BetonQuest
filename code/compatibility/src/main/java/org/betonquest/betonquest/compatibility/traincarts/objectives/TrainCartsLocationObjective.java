package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
     * @param service the objective factory service
     * @param loc     the location the player has to be inside
     * @param range   the range around the location
     * @throws QuestException if there is an error while parsing the instruction
     */
    public TrainCartsLocationObjective(final ObjectiveFactoryService service, final Argument<Location> loc, final Argument<Number> range) throws QuestException {
        super(service);
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
