package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper for the equipment and drops of a mob.
 *
 * @param helmet     the helmet to equip the mob with
 * @param chestplate the chest plate to equip the mob with
 * @param leggings   the leggings to equip the mob with
 * @param boots      the boots to equip the mob with
 * @param mainHand   the main hand item to equip the mob with
 * @param offHand    the off-hand item to equip the mob with
 * @param drops      the items to drop when the mob dies
 */
public record Equipment(@Nullable QuestItem helmet, @Nullable QuestItem chestplate,
                        @Nullable QuestItem leggings, @Nullable QuestItem boots, @Nullable QuestItem mainHand,
                        @Nullable QuestItem offHand, Item[] drops) {

    /**
     * Adds the drops to the mob.
     *
     * @param mob     the mob to add the drops to
     * @param profile the profile to get the drop amounts from
     * @throws QuestException if the variable could not be resolved
     */
    public void addDrops(final Mob mob, @Nullable final Profile profile) throws QuestException {
        int dropIndex = 0;
        for (final Item item : drops) {
            final String value = item.getID().getFullID() + ":" + item.getAmount().getValue(profile).intValue();
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-drops-" + dropIndex);
            mob.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            dropIndex++;
        }
    }

    /**
     * Adds the equipment to the mob and sets the drop chances to 0 for the equipment.
     *
     * @param mob the mob to add the equipment to
     */
    public void addEquipment(final Mob mob) {
        final EntityEquipment equipment = mob.getEquipment();
        equipment.setHelmet(helmet == null ? null : helmet.generate(1));
        equipment.setChestplate(chestplate == null ? null : chestplate.generate(1));
        equipment.setLeggings(leggings == null ? null : leggings.generate(1));
        equipment.setBoots(boots == null ? null : boots.generate(1));
        equipment.setItemInMainHand(mainHand == null ? null : mainHand.generate(1));
        equipment.setItemInOffHand(offHand == null ? null : offHand.generate(1));
        equipment.setHelmetDropChance(0);
        equipment.setChestplateDropChance(0);
        equipment.setLeggingsDropChance(0);
        equipment.setBootsDropChance(0);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHandDropChance(0);
    }
}
