package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.Locale;

/**
 * Resolves to amount of money.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MoneyVariable extends Variable {
    /**
     * The money amount key.
     */
    private static final String MONEY_AMOUNT = "amount";

    private final Type type;

    private int amount;

    public MoneyVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final String instructionString = instruction.next();
        if (MONEY_AMOUNT.equalsIgnoreCase(instructionString)) {
            type = Type.AMOUNT;
        } else if (instructionString.toLowerCase(Locale.ROOT).startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instructionString.substring(5));
            } catch (final NumberFormatException e) {
                throw new InstructionParseException("Could not parse money amount", e);
            }
        } else {
            throw new InstructionParseException("No type specified");
        }
    }

    @Override
    public String getValue(final Profile profile) {
        return switch (type) {
            case AMOUNT -> String.valueOf(VaultIntegrator.getEconomy().getBalance(profile.getPlayer()));
            case LEFT -> String.valueOf(amount - VaultIntegrator.getEconomy().getBalance(profile.getPlayer()));
        };
    }

    private enum Type {
        AMOUNT, LEFT
    }

}
