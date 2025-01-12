package org.betonquest.betonquest.compatibility.vault.condition;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Checks if the player has specified amount of Vault money
 */
public class MoneyCondition implements PlayerCondition {
    /**
     * Economy where the balance will be checked.
     */
    private final Economy economy;

    /**
     * Amount the balance needs to match.
     */
    private final VariableNumber amount;

    /**
     * Create a new Vault Balance Condition.
     *
     * @param economy the economy where the balance will be checked
     * @param amount  the amount the balance needs to match - needs to be positive
     */
    public MoneyCondition(final Economy economy, final VariableNumber amount) {
        this.economy = economy;
        this.amount = amount;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return economy.has(profile.getPlayer(), amount.getValue(profile).doubleValue());
    }
}
