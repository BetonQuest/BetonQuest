package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Checks if the player owns specified amount of shops.
 */
public class HavingShopCondition implements PlayerCondition {

    /**
     * Target shop amount.
     */
    private final VariableNumber amount;

    /**
     * Create a new shop condition.
     *
     * @param amount the required shop amount
     */
    public HavingShopCondition(final VariableNumber amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        int count = amount.getValue(profile).intValue();
        for (final Shopkeeper s : ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers()) {
            if (s instanceof final PlayerShopkeeper shopkeeper && profile.getProfileUUID().equals(shopkeeper.getOwnerUUID())) {
                count--;
                if (count == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
