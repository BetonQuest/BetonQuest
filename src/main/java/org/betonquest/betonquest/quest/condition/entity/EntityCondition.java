package org.betonquest.betonquest.quest.condition.entity;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
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
    private final Map<EntityType, VariableNumber> entityAmounts;

    /**
     * The location of the entity's.
     */
    private final VariableLocation loc;

    /**
     * The range around the location to check for entities.
     */
    private final VariableNumber range;

    /**
     * The name of the entity to check for.
     */
    @Nullable
    private final VariableString name;

    /**
     * The marked entity to check for.
     */
    @Nullable
    private final VariableString marked;

    /**
     * Create a new entity condition.
     *
     * @param entityAmounts the entity amounts
     * @param loc           the location of the entity's
     * @param range         the range around the location to check for entities
     * @param name          the name of the entity to check for
     * @param marked        the marked entity to check for
     */
    public EntityCondition(final Map<EntityType, VariableNumber> entityAmounts, final VariableLocation loc,
                           final VariableNumber range, @Nullable final VariableString name, @Nullable final VariableString marked) {
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
        for (final Map.Entry<EntityType, VariableNumber> entry : entityAmounts.entrySet()) {
            final long count = selectedEntity.stream().filter(entity -> entity.getType() == entry.getKey()).count();
            if (count < entry.getValue().getValue(profile).intValue()) {
                return false;
            }
        }
        return true;
    }
}
