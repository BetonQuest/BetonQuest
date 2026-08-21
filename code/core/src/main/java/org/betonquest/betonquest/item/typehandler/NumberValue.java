package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface NumberValue {

    @Nullable
    static Argument<NumberValue> create(final String key, final String message, final Instruction instruction) throws QuestException {
        return instruction.parse(resolved -> {
            final Map.Entry<Number, Integer> entry = HandlerUtil.getNumberValue(key, message);
            return (NumberValue) new DefaultNumberValue(entry.getKey(), entry.getValue());
        }).get(key).orElse(null);
    }

    Number number();

    int value();

    boolean isValid(int otherValue) throws QuestException;

    record DefaultNumberValue(Number number, int value) implements NumberValue {

        @Override
        public boolean isValid(final int otherValue) {
            return number.isValid(otherValue, value);
        }
    }
}
