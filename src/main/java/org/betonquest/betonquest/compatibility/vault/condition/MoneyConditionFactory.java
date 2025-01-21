package org.betonquest.betonquest.compatibility.vault.condition;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MoneyCondition}s from {@link Instruction}s.
 */
public class MoneyConditionFactory implements PlayerConditionFactory {
    /**
     * Economy where the balance will be retrieved.
     */
    private final Economy economy;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Vault Money Conditions.
     *
     * @param economy the economy where the balance will be checked
     * @param data    the data used for primary server access
     */
    public MoneyConditionFactory(final Economy economy, final PrimaryServerThreadData data) {
        this.economy = economy;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final VariableNumber amount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new PrimaryServerThreadPlayerCondition(new MoneyCondition(economy, amount), data);
    }
}
