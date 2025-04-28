package org.betonquest.betonquest.quest.condition.entity;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
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
    private final Map<EntityType, Variable<Number>> entityAmounts;

    /**
     * The location of the entity's.
     */
    private final Variable<Location> loc;

    /**
     * The range around the location to check for entities.
     */
    private final Variable<Number> range;

    /**
     * The name of the entity to check for.
     */
    @Nullable
    private final Variable<String> name;

    /**
     * The marked entity to check for.
     */
    @Nullable
    private final Variable<String> marked;

    /**
     * Create a new entity condition.
     *
     * @param entityAmounts the entity amounts
     * @param loc           the location of the entity's
     * @param range         the range around the location to check for entities
     * @param name          the name of the entity to check for
     * @param marked        the marked entity to check for
     */
    public EntityCondition(final Map<EntityType, Variable<Number>> entityAmounts, final Variable<Location> loc,
                           final Variable<Number> range, @Nullable final Variable<String> name, @Nullable final Variable<String> marked) {
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
        final String resolvedName = name == null ? null : name.getValue(profile);
        final String resolvedMarked = marked == null ? null : marked.getValue(profile);
        final List<Entity> selectedEntity = EntityUtils.getSelectedEntity(location, resolvedName, resolvedMarked, resolvedRange);
        for (final Map.Entry<EntityType, Variable<Number>> entry : entityAmounts.entrySet()) {
            final long count = selectedEntity.stream().filter(entity -> entity.getType() == entry.getKey()).count();
            if (count < entry.getValue().getValue(profile).intValue()) {
                return false;
            }
        }
        return true;
    }
}
