package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction.Item;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper for the equipment and drops of an entity.
 *
 * @param helmet     the helmet to equip the entity with
 * @param chestplate the chest plate to equip the entity with
 * @param leggings   the leggings to equip the entity with
 * @param boots      the boots to equip the entity with
 * @param mainHand   the main hand item to equip the entity with
 * @param offHand    the off-hand item to equip the entity with
 * @param drops      the items to drop when the entity dies
 */
public record Equipment(@Nullable QuestItem helmet, @Nullable QuestItem chestplate,
                        @Nullable QuestItem leggings, @Nullable QuestItem boots, @Nullable QuestItem mainHand,
                        @Nullable QuestItem offHand, Item[] drops) {

    /**
     * Adds the drops to the entity.
     *
     * @param entity  the entity to add the drops to
     * @param profile the profile to get the drop amounts from
     * @throws QuestRuntimeException if the variable could not be resolved
     */
    public void addDrops(final Entity entity, @Nullable final Profile profile) throws QuestRuntimeException {
        int dropIndex = 0;
        for (final Item item : drops) {
            final String value = item.getID().getFullID() + ":" + item.getAmount().getValue(profile).intValue();
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-drops-" + dropIndex);
            entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            dropIndex++;
        }
    }

    /**
     * Adds the equipment to the entity.
     *
     * @param living the entity to add the equipment to
     */
    public void addEquipment(final LivingEntity living) {
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
