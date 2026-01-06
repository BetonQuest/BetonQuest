package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;

/**
 * This event opens Shopkeeper trade window.
 */
public class OpenShopEvent implements OnlineAction {

    /**
     * Shop to open.
     */
    private final Shopkeeper shopkeeper;

    /**
     * Create a new open shop event.
     *
     * @param shopkeeper the shop to open
     */
    public OpenShopEvent(final Shopkeeper shopkeeper) {
        this.shopkeeper = shopkeeper;
    }

    @Override
    public void execute(final OnlineProfile profile) {
        shopkeeper.openTradingWindow(profile.getPlayer());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
