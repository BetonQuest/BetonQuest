package org.betonquest.betonquest.api.instruction.argument.parser;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.bukkit.Bukkit;

/**
 * This offers default implementations for {@link Argument} to parse common types.
 */
public interface DefaultArgumentParsers {

    /**
     * The default instance of {@link StringParser}.
     */
    StringParser STRING = new StringParser();

    /**
     * The default instance of {@link BooleanParser}.
     */
    BooleanParser BOOLEAN = new BooleanParser();

    /**
     * The default instance of {@link VectorParser}.
     */
    VectorParser VECTOR = new VectorParser();

    /**
     * The default instance of {@link WorldParser}.
     */
    WorldParser WORLD = new WorldParser(Bukkit.getServer());

    /**
     * The default instance of {@link LocationParser}.
     */
    LocationParser LOCATION = new LocationParser(Bukkit.getServer());

    /**
     * The default instance of {@link NumberParser}.
     */
    NumberParser NUMBER = new NumberParser();

    /**
     * The default instance of {@link NumberParser} that checks if the number is not less than zero.
     */
    NumberParser NUMBER_NOT_LESS_THAN_ZERO = new NumberParser(value -> {
        if (value.doubleValue() < 0) {
            throw new QuestException("Value must be greater than or equal to 0: " + value);
        }
    });

    /**
     * The default instance of {@link NumberParser} that checks if the number is not less than one.
     */
    NumberParser NUMBER_NOT_LESS_THAN_ONE = new NumberParser(value -> {
        if (value.doubleValue() <= 0) {
            throw new QuestException("Value must be greater than or equal to 1: " + value);
        }
    });

    /**
     * The default instance of {@link BlockSelectorParser}.
     */
    BlockSelectorParser BLOCK_SELECTOR = new BlockSelectorParser();

    /**
     * The default instance of {@link TextParserToComponentParser}.
     */
    TextParserToComponentParser MESSAGE = new TextParserToComponentParser(BetonQuest.getInstance().getTextParser());

    /**
     * The default instance of {@link UUIDParser}.
     */
    UUIDParser UUID = new UUIDParser();

    /**
     * The default instance of {@link EnumParser}.
     *
     * @param <E>      the type of the enum
     * @param enumType the type of the enum
     * @return the parser for the enum
     */
    static <E extends Enum<E>> Argument<E> forEnum(final Class<E> enumType) {
        return new EnumParser<>(enumType);
    }
}
