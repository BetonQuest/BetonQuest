package org.betonquest.betonquest.compatibility.vault.condition;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link MoneyCondition}s from {@link Instruction}s.
 */
public class MoneyConditionFactory implements PlayerConditionFactory {

    /**
     * Economy where the balance will be retrieved.
     */
    private final Economy economy;

    /**
     * Create a new Factory to create Vault Money Conditions.
     *
     * @param economy the economy where the balance will be checked
     */
    public MoneyConditionFactory(final Economy economy) {
        this.economy = economy;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().atLeast(1).get();
        return new MoneyCondition(economy, amount);
    }
}
