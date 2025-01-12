package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.utils.Utils;

import java.util.UUID;

/**
 * This event opens Shopkeeper trade window.
 */
@SuppressWarnings("PMD.CommentRequired")
public class OpenShopEvent extends QuestEvent {

    private final Shopkeeper shopkeeper;

    public OpenShopEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        final String string = instruction.next();
        try {
            shopkeeper = Utils.getNN(ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByUniqueId(UUID.fromString(string)),
                    "Shopkeeper with this UUID does not exist: '" + string + "'");
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Could not parse UUID: '" + string + "'", e);
        }
    }

    @Override
    protected Void execute(final Profile profile) {
        shopkeeper.openTradingWindow(profile.getOnlineProfile().get().getPlayer());
        return null;
    }
}
