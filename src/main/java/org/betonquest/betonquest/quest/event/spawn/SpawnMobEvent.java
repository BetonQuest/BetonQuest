package org.betonquest.betonquest.quest.event.spawn;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
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
    private final Variable<Location> variableLocation;

    /**
     * The type of mob to spawn.
     */
    private final Variable<EntityType> type;

    /**
     * The equipment and drops of the mob.
     */
    private final Equipment equipment;

    /**
     * The amount of mobs to spawn.
     */
    private final Variable<Number> amount;

    /**
     * The name of the mob.
     */
    @Nullable
    private final Variable<Component> name;

    /**
     * The marked variable.
     */
    @Nullable
    private final Variable<String> marked;

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
    public SpawnMobEvent(final Variable<Location> variableLocation, final Variable<EntityType> type, final Equipment equipment,
                         final Variable<Number> amount, @Nullable final Variable<Component> name, @Nullable final Variable<String> marked) throws QuestException {
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
            final Mob mob = (Mob) location.getWorld().spawnEntity(location, type.getValue(profile));
            this.equipment.addEquipment(profile, mob);
            this.equipment.addDrops(mob, profile);
            if (this.name != null) {
                mob.customName(this.name.getValue(profile));
            }
            if (this.marked != null) {
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                mob.getPersistentDataContainer().set(key, PersistentDataType.STRING, this.marked.getValue(profile));
            }
        }
    }
}
