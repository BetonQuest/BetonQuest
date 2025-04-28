package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.inventory.ItemStack;

/**
 * Requires the player to wear specific armor.
 */
public class ArmorCondition implements OnlineCondition {

    /**
     * Armor to check.
     */
    private final Variable<Item> armorItem;

    /**
     * Creates a new ArmorCondition.
     *
     * @param armorItem the armor item
     */
    public ArmorCondition(final Variable<Item> armorItem) {
        this.armorItem = armorItem;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Item item = armorItem.getValue(profile);
        for (final ItemStack armor : profile.getPlayer().getEquipment().getArmorContents()) {
            if (item.matches(armor)) {
                return true;
            }
        }
        return false;
    }
}
