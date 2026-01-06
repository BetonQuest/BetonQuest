package org.betonquest.betonquest.quest.action.spawn;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns mobs at given location, with given equipment and drops.
 */
public class SpawnMobAction implements NullableAction {

    /**
     * The location to spawn the mob at.
     */
    private final Argument<Location> location;

    /**
     * The type of mob to spawn.
     */
    private final Argument<EntityType> type;

    /**
     * The equipment and drops of the mob.
     */
    private final Equipment equipment;

    /**
     * The amount of mobs to spawn.
     */
    private final Argument<Number> amount;

    /**
     * The name of the mob.
     */
    @Nullable
    private final Argument<Component> name;

    /**
     * The marked flag.
     */
    @Nullable
    private final Argument<String> marked;

    /**
     * Creates a new spawn mob action.
     *
     * @param location  the location to spawn the mob at
     * @param type      the type of mob to spawn
     * @param equipment the equipment and drops of the mob
     * @param amount    the amount of entities to spawn
     * @param name      the name of the mob
     * @param marked    the marked flag
     */
    public SpawnMobAction(final Argument<Location> location, final Argument<EntityType> type, final Equipment equipment,
                          final Argument<Number> amount, @Nullable final Argument<Component> name, @Nullable final Argument<String> marked) {
        this.location = location;
        this.type = type;
        this.equipment = equipment;
        this.amount = amount;
        this.name = name;
        this.marked = marked;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location location = this.location.getValue(profile);
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

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
