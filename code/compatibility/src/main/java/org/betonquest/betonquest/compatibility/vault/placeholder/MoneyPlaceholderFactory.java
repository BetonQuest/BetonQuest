package org.betonquest.betonquest.compatibility.vault.placeholder;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;

import java.util.Locale;

/**
 * Factory to create {@link MoneyPlaceholder}s from {@link Instruction}s.
 */
public class MoneyPlaceholderFactory implements PlayerPlaceholderFactory {

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
     * Create a new Factory to create Vault Money placeholder.
     *
     * @param economy the economy where the balance will be got
     */
    public MoneyPlaceholderFactory(final Economy economy) {
        this.economy = economy;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final String instructionString = instruction.nextElement();
        final QuestFunction<Profile, String> function;
        if (MONEY_AMOUNT.equalsIgnoreCase(instructionString)) {
            function = profile -> String.valueOf(economy.getBalance(profile.getPlayer()));
        } else if (instructionString.toLowerCase(Locale.ROOT).startsWith(MONEY_LEFT)) {
            final Argument<Number> amount = instruction.chainForArgument(instructionString.substring(MONEY_LEFT.length())).number().get();
            function = profile -> String.valueOf(amount.getValue(profile).doubleValue() - economy.getBalance(profile.getPlayer()));
        } else {
            throw new QuestException("No type specified");
        }
        return new MoneyPlaceholder(function);
    }
}
