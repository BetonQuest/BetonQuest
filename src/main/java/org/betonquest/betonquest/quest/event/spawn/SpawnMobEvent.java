package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns mobs at given location, with given equipment and drops.
 */
public class SpawnMobEvent implements ComposedEvent {

    /**
     * The location to spawn the mob at.
     */
    private final VariableLocation variableLocation;

    /**
     * The type of entity to spawn.
     */
    private final EntityType type;

    /**
     * The amount of entities to spawn.
     */
    private final VariableNumber amount;

    /**
     * The helmet to equip the entity with.
     */
    @Nullable
    private final QuestItem helmet;

    /**
     * The chest plate to equip the entity with.
     */
    @Nullable
    private final QuestItem chestplate;

    /**
     * The leggings to equip the entity with.
     */
    @Nullable
    private final QuestItem leggings;

    /**
     * The boots to equip the entity with.
     */
    @Nullable
    private final QuestItem boots;

    /**
     * The main hand item to equip the entity with.
     */
    @Nullable
    private final QuestItem mainHand;

    /**
     * The off-hand item to equip the entity with.
     */
    @Nullable
    private final QuestItem offHand;

    /**
     * The items to drop when the entity dies.
     */
    private final Item[] drops;

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
     * @param amount           the amount of entities to spawn
     * @param helmet           the helmet to equip the entity with
     * @param chestplate       the chest plate to equip the entity with
     * @param leggings         the leggings to equip the entity with
     * @param boots            the boots to equip the entity with
     * @param mainHand         the main hand item to equip the entity with
     * @param offHand          the off-hand item to equip the entity with
     * @param drops            the items to drop when the entity dies
     * @param name             the name of the entity
     * @param marked           the marked variable
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public SpawnMobEvent(final VariableLocation variableLocation, final EntityType type, final VariableNumber amount,
                         @Nullable final QuestItem helmet, @Nullable final QuestItem chestplate,
                         @Nullable final QuestItem leggings, @Nullable final QuestItem boots,
                         @Nullable final QuestItem mainHand, @Nullable final QuestItem offHand,
                         final Item[] drops, @Nullable final VariableString name, @Nullable final VariableString marked) {
        this.variableLocation = variableLocation;
        this.type = type;
        this.amount = amount;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.drops = drops.clone();
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
                addEquipment(living);
            }
            addDrops(entity, profile);
            if (name != null && entity instanceof final LivingEntity livingEntity) {
                livingEntity.setCustomName(name.getValue(profile));
            }
            if (marked != null) {
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-marked");
                entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, marked.getValue(profile));
            }
        }
    }

    private void addDrops(final Entity entity, @Nullable final Profile profile) throws QuestRuntimeException {
        int dropIndex = 0;
        for (final Item item : drops) {
            final String value = item.getID().getFullID() + ":" + item.getAmount().getValue(profile);
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-drops-" + dropIndex);
            entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            dropIndex++;
        }
    }

    private void addEquipment(final LivingEntity living) {
        final EntityEquipment equipment = living.getEquipment();
        if (equipment != null) {
            equipment.setHelmet(helmet == null ? null : helmet.generate(1));
            equipment.setHelmetDropChance(0);
            equipment.setChestplate(chestplate == null ? null : chestplate.generate(1));
            equipment.setChestplateDropChance(0);
            equipment.setLeggings(leggings == null ? null : leggings.generate(1));
            equipment.setLeggingsDropChance(0);
            equipment.setBoots(boots == null ? null : boots.generate(1));
            equipment.setBootsDropChance(0);
            equipment.setItemInMainHand(mainHand == null ? null : mainHand.generate(1));
            equipment.setItemInMainHandDropChance(0);
            equipment.setItemInOffHand(offHand == null ? null : offHand.generate(1));
            equipment.setItemInOffHandDropChance(0);
        }
    }

}
