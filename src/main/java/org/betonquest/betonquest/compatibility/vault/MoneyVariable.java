package org.betonquest.betonquest.compatibility.vault;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.Locale;

/**
 * Resolves to amount of money.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MoneyVariable extends Variable {

    private Type type;
    private int amount;

    public MoneyVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        if ("amount".equalsIgnoreCase(instruction.next())) {
            type = Type.AMOUNT;
        } else if (instruction.current().toLowerCase(Locale.ROOT).startsWith("left:")) {
            type = Type.LEFT;
            try {
                amount = Integer.parseInt(instruction.current().substring(5));
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse money amount", e);
            }
        }
    }

    @Override
    public String getValue(final String playerID) {
        switch (type) {
            case AMOUNT:
                return String.valueOf(VaultIntegrator.getEconomy().getBalance(PlayerConverter.getPlayer(playerID)));
            case LEFT:
                return String.valueOf(amount - VaultIntegrator.getEconomy().getBalance(PlayerConverter.getPlayer(playerID)));
            default:
                return "";
        }
    }

    private enum Type {
        AMOUNT, LEFT
    }

}
