package org.betonquest.betonquest.quest.event.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.util.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Removes all entities of given type at location.
 */
public class RemoveEntityEvent implements NullableEvent {

    /**
     * The type of the mob.
     */
    private final Argument<List<EntityType>> types;

    /**
     * The location of the mob.
     */
    private final Argument<Location> loc;

    /**
     * The radius around the location.
     */
    private final Argument<Number> range;

    /**
     * The name of the mob.
     */
    @Nullable
    private final Argument<Component> name;

    /**
     * The mark of the mob.
     */
    @Nullable
    private final Argument<String> marked;

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
    public RemoveEntityEvent(final Argument<List<EntityType>> types, final Argument<Location> location, final Argument<Number> radius,
                             @Nullable final Argument<Component> name, @Nullable final Argument<String> marked, final boolean kill) {
        this.types = types;
        this.loc = location;
        this.range = radius;
        this.name = name;
        this.marked = marked;
        this.kill = kill;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location location = loc.getValue(profile);
        final Component name = this.name == null ? null : this.name.getValue(profile);
        final String resolvedMarked = marked == null ? null : marked.getValue(profile);
        final double resolvedRange = range.getValue(profile).doubleValue();
        final List<Entity> entities = EntityUtils.getSelectedEntity(location, name, resolvedMarked, resolvedRange);
        for (final EntityType type : types.getValue(profile)) {
            entities.stream()
                    .filter(entity -> entity.getType() == type)
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
