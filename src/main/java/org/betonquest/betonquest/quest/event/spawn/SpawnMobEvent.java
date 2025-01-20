package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns mobs at given location, with given equipment and drops.
 */
public class SpawnMobEvent implements NullableEvent {

    /**
     * The location to spawn the mob at.
     */
    private final VariableLocation variableLocation;

    /**
     * The type of mob to spawn.
     */
    private final EntityType type;

    /**
     * The equipment and drops of the mob.
     */
    private final Equipment equipment;

    /**
     * The amount of mobs to spawn.
     */
    private final VariableNumber amount;

    /**
     * The name of the mob.
     */
    @Nullable
    private final VariableString name;

    /**
     * The marked variable.
     */
    @Nullable
    private final VariableString marked;

    /**
     * Creates a new spawn mob event.
     *
     * @param variableLocation the location to spawn the mob at
     * @param type             the type of mob to spawn
     * @param equipment        the equipment and drops of the mob
     * @param amount           the amount of entities to spawn
     * @param name             the name of the mob
     * @param marked           the marked variable
     * @throws QuestException if the entity type is not a mob
     */
    public SpawnMobEvent(final VariableLocation variableLocation, final EntityType type, final Equipment equipment,
                         final VariableNumber amount, @Nullable final VariableString name, @Nullable final VariableString marked) throws QuestException {
        if (type.getEntityClass() == null || !Mob.class.isAssignableFrom(type.getEntityClass())) {
            throw new QuestException("The entity type must be a mob");
        }
        this.variableLocation = variableLocation;
        this.type = type;
        this.equipment = equipment;
        this.amount = amount;
        this.name = name;
        this.marked = marked;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location location = variableLocation.getValue(profile);
        final int numberOfMob = amount.getValue(profile).intValue();
        for (int i = 0; i < numberOfMob; i++) {
            final Mob mob = (Mob) location.getWorld().spawnEntity(location, type);
            this.equipment.addEquipment(mob);
            this.equipment.addDrops(mob, profile);
            if (this.name != null) {
                mob.setCustomName(this.name.getValue(profile));
            }
            if (this.marked != null) {
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                mob.getPersistentDataContainer().set(key, PersistentDataType.STRING, this.marked.getValue(profile));
            }
        }
    }
}
