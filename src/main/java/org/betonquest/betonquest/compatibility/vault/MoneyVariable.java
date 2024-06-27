package org.betonquest.betonquest.compatibility.vault;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(MoneyVariable.class);

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
    public String getValue(@Nullable final Profile profile) {
        if (profile == null) {
            return "";
        }
        final Economy economy = VaultIntegrator.getInstance().getEconomy();
        if (economy == null) {
            log.warn("Can't get Variable value because the Vault instance is null!");
            return "";
        }
        return switch (type) {
            case AMOUNT -> String.valueOf(economy.getBalance(profile.getPlayer()));
            case LEFT -> String.valueOf(amount - economy.getBalance(profile.getPlayer()));
        };
    }

    private enum Type {
        AMOUNT, LEFT
    }
}
