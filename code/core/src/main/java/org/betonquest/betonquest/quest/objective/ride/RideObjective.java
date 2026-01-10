package org.betonquest.betonquest.quest.objective.ride;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.bukkit.entity.EntityType;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Optional;

/**
 * Requires the player to ride a vehicle.
 */
public class RideObjective extends DefaultObjective {

    /**
     * The type of vehicle that is required or an empty optional if any vehicle is allowed.
     */
    private final Argument<Optional<EntityType>> vehicle;

    /**
     * Constructor for the RideObjective.
     *
     * @param service the objective factory service
     * @param vehicle the type of vehicle that is required, or null if any vehicle is allowed
     * @throws QuestException if there is an error in the instruction
     */
    public RideObjective(final ObjectiveService service, final Argument<Optional<EntityType>> vehicle) throws QuestException {
        super(service);
        this.vehicle = vehicle;
    }

    /**
     * Check if the player is riding the right vehicle.
     *
     * @param event         the event to check
     * @param onlineProfile the profile of the player that rides the vehicle
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onMount(final EntityMountEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final Optional<EntityType> entityType = vehicle.getValue(onlineProfile);
        final boolean matchType = entityType.map(type -> type == event.getMount().getType()).orElse(true);
        if (matchType) {
            getService().complete(onlineProfile);
        }
    }
}
