package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Checks if the player owns specified amount of shops.
 */
@SuppressWarnings("PMD.CommentRequired")
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
            if (!(s instanceof PlayerShopkeeper)) {
                continue;
            }
            final PlayerShopkeeper shopkeeper = (PlayerShopkeeper) s;
            if (shopkeeper.getOwnerUUID() == null || !shopkeeper.getOwnerUUID().toString().equals(playerID)) {
                continue;
            }
            count--;
            if (count == 0) {
                return true;
            }
        }
        return false;
    }

}
