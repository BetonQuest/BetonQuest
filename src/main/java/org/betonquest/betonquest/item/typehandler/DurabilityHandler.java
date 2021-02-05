package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Number;

@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidUsingShortType"})
public class DurabilityHandler {

    private short durability;
    private Number number = Number.WHATEVER;

    public DurabilityHandler() {
    }

    public void set(final String durability) throws InstructionParseException {
        String inputDurability = durability;
        if (inputDurability.endsWith("-")) {
            number = Number.LESS;
            inputDurability = inputDurability.substring(0, inputDurability.length() - 1);
        } else if (inputDurability.endsWith("+")) {
            number = Number.MORE;
            inputDurability = inputDurability.substring(0, inputDurability.length() - 1);
        } else {
            number = Number.EQUAL;
        }
        try {
            this.durability = Short.valueOf(inputDurability);
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
