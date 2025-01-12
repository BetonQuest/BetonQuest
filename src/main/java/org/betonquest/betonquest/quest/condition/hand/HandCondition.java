package org.betonquest.betonquest.quest.condition.hand;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.PlayerInventory;

/**
 * Condition to check if a player is holding an item in their hand.
 */
public class HandCondition implements OnlineCondition {

    /**
     * The item to check for.
     */
    private final QuestItem questItem;

    /**
     * Whether the item is in the offhand.
     */
    private final boolean offhand;

    /**
     * Creates a new hand condition.
     *
     * @param questItem the item to check for
     * @param offhand   whether the item is in the offhand
     */
    public HandCondition(final QuestItem questItem, final boolean offhand) {
        this.questItem = questItem;
        this.offhand = offhand;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final PlayerInventory inv = profile.getPlayer().getInventory();
        return questItem.compare(offhand ? inv.getItemInOffHand() : inv.getItemInMainHand());
    }
}
