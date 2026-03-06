package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.UUID;

/**
 * Factory to create {@link OpenShopAction}s from {@link Instruction}s.
 */
public class OpenShopActionFactory implements PlayerActionFactory {

    /**
     * Create a new open shop action factory.
     */
    public OpenShopActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.string().get().getValue(null);
        final Shopkeeper shopkeeper;
        try {
            shopkeeper = ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByUniqueId(UUID.fromString(string));
            if (shopkeeper == null) {
                throw new QuestException("Shopkeeper with the UUID '%s' does not exist!".formatted(string));
            }
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Could not parse UUID: '" + string + "'", e);
        }
        return new OnlineActionAdapter(new OpenShopAction(shopkeeper));
    }
}
