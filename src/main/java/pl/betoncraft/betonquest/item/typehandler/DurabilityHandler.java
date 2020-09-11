package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Number;

public class DurabilityHandler {

    private short durability = 0;
    private Number number = Number.WHATEVER;

    public DurabilityHandler() {}

    public void set(String durability) throws InstructionParseException {
        if (durability.endsWith("-")) {
            number = Number.LESS;
            durability = durability.substring(0, durability.length() - 1);
        } else if (durability.endsWith("+")) {
            number = Number.MORE;
            durability = durability.substring(0, durability.length() - 1);
        } else {
            number = Number.EQUAL;
        }
        try {
            this.durability = Short.valueOf(durability);
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse item durability value", e);
        }
    }

    public short get() {
        return durability;
    }

    public boolean check(final int durability) {
        switch (number) {
            case WHATEVER:
                return true;
            case EQUAL:
                return this.durability == durability;
            case MORE:
                return this.durability <= durability;
            case LESS:
                return this.durability >= durability;
            default:
                return false;
        }
    }

    /**
     * @return checks if the state of this type handler should be ignored
     */
    public boolean whatever() {
        return number == Number.WHATEVER;
    }

}
