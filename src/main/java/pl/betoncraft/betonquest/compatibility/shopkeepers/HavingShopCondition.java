package pl.betoncraft.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if the player owns specified amount of shops.
 */
public class HavingShopCondition extends Condition {

    private final VariableNumber amount;

    public HavingShopCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        persistent = true;
        amount = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        int count = amount.getInt(playerID);
        for (final Shopkeeper s : ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers()) {
            if (s instanceof PlayerShopkeeper) {
                final PlayerShopkeeper shopkeeper = (PlayerShopkeeper) s;
                if (shopkeeper.getOwnerUUID() != null && shopkeeper.getOwnerUUID().toString().equals(playerID)) {
                    count--;
                    if (count == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
