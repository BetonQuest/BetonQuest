package org.betonquest.betonquest.compatibility.vault.variable;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
     * Logger factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory to create Vault Money Variables.
     *
     * @param economy       the economy where the balance will be got
     * @param loggerFactory the logger factory to create new class specific loggers
     */
    public MoneyVariableFactory(final Economy economy, final BetonQuestLoggerFactory loggerFactory) {
        this.economy = economy;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String instructionString = instruction.next();
        final QREFunction<Profile, String> function;
        if (MONEY_AMOUNT.equalsIgnoreCase(instructionString)) {
            function = profile -> String.valueOf(economy.getBalance(profile.getPlayer()));
        } else if (instructionString.toLowerCase(Locale.ROOT).startsWith(MONEY_LEFT)) {
            final VariableNumber amount = instruction.getVarNum(instructionString.substring(MONEY_LEFT.length()));
            function = profile -> String.valueOf(amount.getValue(profile).doubleValue() - economy.getBalance(profile.getPlayer()));
        } else {
            throw new InstructionParseException("No type specified");
        }
        return new MoneyVariable(function, loggerFactory.create(MoneyVariable.class), instruction.getPackage());
    }

    /**
     * A simple {@link java.util.function.Function} that can throw a QuestRuntimeException.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     */
    public interface QREFunction<T, R> {
        /**
         * Applies this function to the given argument.
         *
         * @param arg the function argument
         * @return the function result
         * @throws QuestRuntimeException if the resolving fails
         */
        R apply(T arg) throws QuestRuntimeException;
    }
}
