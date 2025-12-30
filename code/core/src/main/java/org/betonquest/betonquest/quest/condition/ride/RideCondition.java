package org.betonquest.betonquest.quest.condition.ride;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Optional;

/**
 * A condition that checks if the player is riding a vehicle.
 */
public class RideCondition implements OnlineCondition {

    /**
     * The entity type to match or an empty optional if any entity is allowed.
     */
    private final Argument<Optional<EntityType>> vehicle;

    /**
     * Creates a new ride condition. If the entity type is null, any entity will match.
     *
     * @param vehicle the entity type to match or an empty optional if any entity is allowed
     */
    public RideCondition(final Argument<Optional<EntityType>> vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Entity entity = profile.getPlayer().getVehicle();
        if (entity == null) {
            return false;
        }
        final Optional<EntityType> entityType = vehicle.getValue(profile);
        return entityType.map(type -> type == entity.getType()).orElse(true);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
