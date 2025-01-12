package org.betonquest.betonquest.quest.condition.ride;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

/**
 * A condition that checks if the player is riding a vehicle.
 */
public class RideCondition implements OnlineCondition {

    /**
     * The entity type to match.
     */
    @Nullable
    private final EntityType vehicle;

    /**
     * Creates a new ride condition. If the entity type is null, any entity will match.
     *
     * @param vehicle the entity type to match
     */
    public RideCondition(@Nullable final EntityType vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Entity entity = profile.getPlayer().getVehicle();
        return entity != null && (vehicle == null || entity.getType() == vehicle);
    }
}
