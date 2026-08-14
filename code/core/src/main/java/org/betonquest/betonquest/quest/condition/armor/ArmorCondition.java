package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Requires the player to wear specific armor.
 */
public class ArmorCondition implements OnlineCondition {

    /**
     * Armor to check.
     */
    private final Argument<ItemWrapper> armorItem;

    /**
     * Slots to check, if not all slots should be checked.
     */
    @Nullable
    private final Argument<List<EquipmentSlot>> slots;

    /**
     * Creates a new ArmorCondition.
     *
     * @param armorItem the armor item
     * @param slots     the slots to check, if not all slots should be checked
     */
    public ArmorCondition(final Argument<ItemWrapper> armorItem, @Nullable final Argument<List<EquipmentSlot>> slots) {
        this.armorItem = armorItem;
        this.slots = slots;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final ItemWrapper item = armorItem.getValue(profile);
        final EntityEquipment equipment = profile.getPlayer().getEquipment();
        if (slots == null) {
            for (final ItemStack armor : equipment.getArmorContents()) {
                if (item.matches(armor, profile)) {
                    return true;
                }
            }
        } else {
            for (final EquipmentSlot slot : slots.getValue(profile)) {
                if (item.matches(equipment.getItem(slot), profile)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
