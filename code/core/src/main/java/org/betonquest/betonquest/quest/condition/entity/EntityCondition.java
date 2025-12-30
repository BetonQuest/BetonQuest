package org.betonquest.betonquest.quest.condition.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.util.EntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A condition that checks if there are specified entities in the area.
 */
public class EntityCondition implements NullableCondition {

    /**
     * The amount per entity.
     */
    private final Argument<List<Map.Entry<EntityType, Integer>>> entityAmounts;

    /**
     * The location of the entity's.
     */
    private final Argument<Location> loc;

    /**
     * The range around the location to check for entities.
     */
    private final Argument<Number> range;

    /**
     * The name of the entity to check for.
     */
    @Nullable
    private final Argument<Component> name;

    /**
     * The marked entity to check for.
     */
    @Nullable
    private final Argument<String> marked;

    /**
     * Create a new entity condition.
     *
     * @param entityAmounts the amount per entity
     * @param loc           the location of the entity's
     * @param range         the range around the location to check for entities
     * @param name          the name of the entity to check for
     * @param marked        the marked entity to check for
     */
    public EntityCondition(final Argument<List<Map.Entry<EntityType, Integer>>> entityAmounts, final Argument<Location> loc,
                           final Argument<Number> range, @Nullable final Argument<Component> name, @Nullable final Argument<String> marked) {
        this.entityAmounts = entityAmounts;
        this.loc = loc;
        this.range = range;
        this.name = name;
        this.marked = marked;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final Location location = loc.getValue(profile);
        final double resolvedRange = range.getValue(profile).doubleValue();
        final Component name = this.name == null ? null : this.name.getValue(profile);
        final String resolvedMarked = marked == null ? null : marked.getValue(profile);
        final List<Entity> selectedEntity = EntityUtils.getSelectedEntity(location, name, resolvedMarked, resolvedRange);
        for (final Map.Entry<EntityType, Integer> entry : entityAmounts.getValue(profile)) {
            final long count = selectedEntity.stream().filter(entity -> entity.getType() == entry.getKey()).count();
            if (count < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
