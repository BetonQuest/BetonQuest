package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        double pAmount = amount.getValue(profile).doubleValue();
        if (pAmount < 0) {
            pAmount = -pAmount;
        }
        final Economy economy = VaultIntegrator.getInstance().getEconomy();
        if (economy == null) {
            throw new QuestRuntimeException("Can't check the condition because the Vault instance is null!");
        }
        return economy.has(profile.getPlayer(), pAmount);
    }
}
