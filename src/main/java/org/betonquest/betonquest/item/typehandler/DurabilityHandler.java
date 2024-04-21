package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.item.QuestItem.Number;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class DurabilityHandler {
    private int durability;

    private Number number = Number.WHATEVER;

    public DurabilityHandler() {
    }

    public void set(final String durability) throws InstructionParseException {
        final Map.Entry<Number, Integer> itemDurability = HandlerUtil.getNumberValue(durability, "item durability");
        this.number = itemDurability.getKey();
        this.durability = itemDurability.getValue();
    }

    public int get() {
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
