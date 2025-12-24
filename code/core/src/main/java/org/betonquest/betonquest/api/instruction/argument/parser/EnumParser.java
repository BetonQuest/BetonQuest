package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;

import java.util.Locale;

/**
 * Parses a string to an enum.
 *
 * @param <T> the type of the enum
 */
public class EnumParser<T extends Enum<T>> implements SimpleArgumentParser<T> {

    /**
     * The type of the enum.
     */
    private final Class<T> enumType;

    /**
     * Creates a new parser for enums.
     *
     * @param enumType the type of the enum
     */
    public EnumParser(final Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T apply(final String string) throws QuestException {
        final String upperValue = string.toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(enumType, upperValue);
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Invalid enum value '" + upperValue + "' for type '" + enumType.getSimpleName() + "'", e);
        }
    }
}
