package pl.betoncraft.betonquest.compatibility.vault;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if the player has specified amount of Vault money
 */
@SuppressWarnings("PMD.CommentRequired")
public class MoneyCondition extends Condition {

    private final VariableNumber amount;

    public MoneyCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        amount = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        double pAmount = amount.getDouble(playerID);
        if (pAmount < 0) {
            pAmount = -pAmount;
        }
        return VaultIntegrator.getEconomy().has(PlayerConverter.getPlayer(playerID), pAmount);
    }

}
