package org.betonquest.betonquest.quest.event.entity;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Removes all entities of given type at location.
 * <p>
 * Created on 29.06.2018.
 */
public class RemoveEntityEvent implements Event {

    /**
     * The type of the mob.
     */
    private final EntityType[] types;

    /**
     * The location of the mob.
     */
    private final CompoundLocation location;

    /**
     * The radius around the location.
     */
    private final VariableNumber radius;

    /**
     * The name of the mob.
     */
    private final String name;

    /**
     * The mark of the mob.
     */
    private final VariableString marked;

    /**
     * Whether to kill the mob.
     */
    private final boolean kill;

    /**
     * Creates a new KillMobEvent.
     *
     * @param types    the types of the entities
     * @param location the location where to remove the entities
     * @param radius   the radius around the location
     * @param name     the name of the entity
     * @param marked   the mark of the entity
     * @param kill     whether to kill the entities
     */
    public RemoveEntityEvent(final EntityType[] types, final CompoundLocation location, final VariableNumber radius,
                             @Nullable final String name, @Nullable final VariableString marked, final boolean kill) {
        this.types = Arrays.copyOf(types, types.length);
        this.location = location;
        this.radius = radius;
        this.name = name;
        this.marked = marked;
        this.kill = kill;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Location mobLocation = location.getLocation(profile);
        for (final EntityType type : types) {
            mobLocation.getNearbyEntitiesByType(type.getEntityClass(), radius.getDouble(profile))
                    .stream()
                    .filter(entity -> {
                        if (name == null) {
                            return true;
                        }
                        return name.equals(entity.getName());
                    })
                    .filter(entity -> {
                        if (marked == null) {
                            return true;
                        }
                        return entity
                                .getMetadata("betonquest-marked")
                                .stream()
                                .anyMatch(metadataValue -> metadataValue.asString().equals(marked.getString(profile)));
                    })
                    .forEach(entity -> {
                                if (kill && entity instanceof final LivingEntity mob) {
                                    mob.setHealth(0);
                                } else {
                                    entity.remove();
                                }
                            }
                    );
        }
    }
}
