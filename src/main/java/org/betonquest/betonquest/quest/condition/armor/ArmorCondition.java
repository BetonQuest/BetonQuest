package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;

/**
 * Requires the player to wear specific armor.
 */
public class ArmorCondition implements OnlineCondition {

    /**
     * Armor to check.
     */
    private final QuestItem armorItem;

    /**
     * Creates a new ArmorCondition.
     *
     * @param armorItem the armor item
     */
    public ArmorCondition(final QuestItem armorItem) {
        this.armorItem = armorItem;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        for (final ItemStack armor : profile.getPlayer().getEquipment().getArmorContents()) {
            if (armorItem.compare(armor)) {
                return true;
            }
        }
        return false;
    }
}
