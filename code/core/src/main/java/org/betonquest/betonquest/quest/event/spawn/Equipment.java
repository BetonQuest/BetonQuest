package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
public record Equipment(@Nullable Argument<ItemWrapper> helmet, @Nullable Argument<ItemWrapper> chestplate,
                        @Nullable Argument<ItemWrapper> leggings, @Nullable Argument<ItemWrapper> boots,
                        @Nullable Argument<ItemWrapper> mainHand, @Nullable Argument<ItemWrapper> offHand,
                        Argument<List<ItemWrapper>> drops) {

    /**
     * Adds the drops to the mob.
     *
     * @param mob     the mob to add the drops to
     * @param profile the profile to get the drop amounts from
     * @throws QuestException if the argument could not be resolved
     */
    public void addDrops(final Mob mob, @Nullable final Profile profile) throws QuestException {
        int dropIndex = 0;
        for (final ItemWrapper item : drops.getValue(profile)) {
            final String value = item.getID().getFull() + ":" + item.getAmount().getValue(profile).intValue();
            final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "betonquest-drops-" + dropIndex);
            mob.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            dropIndex++;
        }
    }

    /**
     * Adds the equipment to the mob and sets the drop chances to 0 for the equipment.
     *
     * @param profile the profile to get the equipment from
     * @param mob     the mob to add the equipment to
     * @throws QuestException if a QuestItem does not exist
     */
    public void addEquipment(@Nullable final Profile profile, final Mob mob) throws QuestException {
        final EntityEquipment equipment = mob.getEquipment();
        equipment.setHelmet(generate(profile, helmet));
        equipment.setChestplate(generate(profile, chestplate));
        equipment.setLeggings(generate(profile, leggings));
        equipment.setBoots(generate(profile, boots));
        equipment.setItemInMainHand(generate(profile, mainHand));
        equipment.setItemInOffHand(generate(profile, offHand));
        equipment.setHelmetDropChance(0);
        equipment.setChestplateDropChance(0);
        equipment.setLeggingsDropChance(0);
        equipment.setBootsDropChance(0);
        equipment.setItemInMainHandDropChance(0);
        equipment.setItemInOffHandDropChance(0);
    }

    @Nullable
    private ItemStack generate(@Nullable final Profile profile, @Nullable final Argument<ItemWrapper> item) throws QuestException {
        return item == null ? null : item.getValue(profile).getItem(profile).generate(1);
    }
}
