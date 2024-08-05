package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
     * The type of entity to spawn.
     */
    private final EntityType type;

    /**
     * The equipment and drops of the entity.
     */
    private final Equipment equipment;

    /**
     * The amount of entities to spawn.
     */
    private final VariableNumber amount;

    /**
     * The name of the entity.
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
     * @param type             the type of entity to spawn
     * @param equipment        the equipment and drops of the entity
     * @param amount           the amount of entities to spawn
     * @param name             the name of the entity
     * @param marked           the marked variable
     */
    public SpawnMobEvent(final VariableLocation variableLocation, final EntityType type, final Equipment equipment,
                         final VariableNumber amount, @Nullable final VariableString name, @Nullable final VariableString marked) {
        this.variableLocation = variableLocation;
        this.type = type;
        this.equipment = equipment;
        this.amount = amount;
        this.name = name;
        this.marked = marked;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Location location = variableLocation.getValue(profile);
        final int numberOfEntity = amount.getValue(profile).intValue();
        for (int i = 0; i < numberOfEntity; i++) {
            final Entity entity = location.getWorld().spawnEntity(location, type);
            if (entity instanceof final LivingEntity living) {
                this.equipment.addEquipment(living);
            }
            this.equipment.addDrops(entity, profile);
            if (this.name != null && entity instanceof final LivingEntity livingEntity) {
                livingEntity.setCustomName(this.name.getValue(profile));
            }
            if (this.marked != null) {
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, this.marked.getValue(profile));
            }
        }
    }

}
