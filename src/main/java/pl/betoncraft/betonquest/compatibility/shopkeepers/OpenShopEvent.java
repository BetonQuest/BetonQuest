package pl.betoncraft.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.UUID;

/**
 * This event opens Shopkeeper trade window.
 */
public class OpenShopEvent extends QuestEvent {

    private Shopkeeper shopkeeper;

    public OpenShopEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        try {
            shopkeeper = ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByUniqueId(UUID.fromString(string));
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Could not parse UUID: '" + string + "'", e);
        }
        if (shopkeeper == null) {
            throw new InstructionParseException("Shopkeeper with this UUID does not exist: '" + string + "'");
        }
    }

    @Override
    protected Void execute(final String playerID) {
        shopkeeper.openTradingWindow(PlayerConverter.getPlayer(playerID));
        return null;
    }

}
