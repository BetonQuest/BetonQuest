package org.betonquest.betonquest.compatibility.vault.variable;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.Locale;

/**
 * Factory to create {@link MoneyVariable}s from {@link Instruction}s.
 */
public class MoneyVariableFactory implements PlayerVariableFactory {
    /**
     * The money amount key.
     */
    private static final String MONEY_AMOUNT = "amount";

    /**
     * The money left key.
     */
    private static final String MONEY_LEFT = "left:";

    /**
     * Economy where the balance will be got.
     */
    private final Economy economy;

    /**
     * Create a new Factory to create Vault Money Variables.
     *
     * @param economy the economy where the balance will be got
     */
    public MoneyVariableFactory(final Economy economy) {
        this.economy = economy;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final String instructionString = instruction.next();
        final QuestFunction<Profile, String> function;
        if (MONEY_AMOUNT.equalsIgnoreCase(instructionString)) {
            function = profile -> String.valueOf(economy.getBalance(profile.getPlayer()));
        } else if (instructionString.toLowerCase(Locale.ROOT).startsWith(MONEY_LEFT)) {
            final VariableNumber amount = instruction.get(instructionString.substring(MONEY_LEFT.length()), VariableNumber::new);
            function = profile -> String.valueOf(amount.getValue(profile).doubleValue() - economy.getBalance(profile.getPlayer()));
        } else {
            throw new QuestException("No type specified");
        }
        return new MoneyVariable(function);
    }
}
