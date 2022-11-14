package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

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
        double pAmount = amount.getDouble(profile);
        if (pAmount < 0) {
            pAmount = -pAmount;
        }
        return VaultIntegrator.getEconomy().has(profile.getPlayer(), pAmount);
    }

}
