package org.betonquest.betonquest.quest.condition.hand;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.inventory.PlayerInventory;

/**
 * Condition to check if a player is holding an item in their hand.
 */
public class HandCondition implements OnlineCondition {

    /**
     * The item to check for.
     */
    private final Variable<Item> item;

    /**
     * Whether the item is in the offhand.
     */
    private final boolean offhand;

    /**
     * Creates a new hand condition.
     *
     * @param item    the item to check for
     * @param offhand whether the item is in the offhand
     */
    public HandCondition(final Variable<Item> item, final boolean offhand) {
        this.item = item;
        this.offhand = offhand;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final PlayerInventory inv = profile.getPlayer().getInventory();
        return item.getValue(profile).matches(offhand ? inv.getItemInOffHand() : inv.getItemInMainHand(), profile);
    }
}
