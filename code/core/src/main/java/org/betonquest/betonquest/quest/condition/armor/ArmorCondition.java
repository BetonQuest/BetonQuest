package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.inventory.ItemStack;

/**
 * Requires the player to wear specific armor.
 */
public class ArmorCondition implements OnlineCondition {

    /**
     * Armor to check.
     */
    private final Argument<ItemWrapper> armorItem;

    /**
     * Creates a new ArmorCondition.
     *
     * @param armorItem the armor item
     */
    public ArmorCondition(final Argument<ItemWrapper> armorItem) {
        this.armorItem = armorItem;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final ItemWrapper item = armorItem.getValue(profile);
        for (final ItemStack armor : profile.getPlayer().getEquipment().getArmorContents()) {
            if (item.matches(armor, profile)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
