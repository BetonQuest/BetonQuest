package pl.betoncraft.betonquest.compatibility.vault;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Resolves to amount of money.
 */
public class MoneyVariable extends Variable {

    private Type type;
    private int amount;

    public MoneyVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        if (instruction.next().equalsIgnoreCase("amount")) {
            type = Type.AMOUNT;
        } else if (instruction.current().toLowerCase().startsWith("left:")) {
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
