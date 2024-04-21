package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Number;

@SuppressWarnings("PMD.CommentRequired")
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
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse item durability value", e);
        }
    }

    public short get() {
        return durability;
    }

    public boolean check(final int durability) {
        return switch (number) {
            case WHATEVER -> true;
            case EQUAL -> this.durability == durability;
            case MORE -> this.durability <= durability;
            case LESS -> this.durability >= durability;
        };
    }

    /**
     * @return checks if the state of this type handler should be ignored
     */
    public boolean whatever() {
        return number == Number.WHATEVER;
    }

}
