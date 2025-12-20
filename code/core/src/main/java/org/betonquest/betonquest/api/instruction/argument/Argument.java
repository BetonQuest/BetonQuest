package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.types.BlockSelectorParser;
import org.betonquest.betonquest.api.instruction.argument.types.BooleanParser;
import org.betonquest.betonquest.api.instruction.argument.types.EnumParser;
import org.betonquest.betonquest.api.instruction.argument.types.NumberParser;
import org.betonquest.betonquest.api.instruction.argument.types.StringParser;
import org.betonquest.betonquest.api.instruction.argument.types.TextParserToComponentParser;
import org.betonquest.betonquest.api.instruction.argument.types.UUIDParser;
import org.betonquest.betonquest.api.instruction.argument.types.location.LocationParser;
import org.betonquest.betonquest.api.instruction.argument.types.location.VectorParser;
import org.betonquest.betonquest.api.instruction.argument.types.location.WorldParser;
import org.betonquest.betonquest.api.instruction.variable.VariableResolver;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Objectified parser for the Instruction to get a {@link T} from string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface Argument<T> extends VariableResolver<T> {

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
    @SuppressWarnings("PMD.MethodNamingConventions")
    static <E extends Enum<E>> Argument<E> ENUM(final Class<E> enumType) {
        return new EnumParser<>(enumType);
    }

    /**
     * Gets a {@link T} from string.
     *
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    @Override
    T apply(String string) throws QuestException;

    /**
     * Returns a new {@link Argument} that checks for the given expected string before
     * applying the {@link Argument} this method is called on.
     * If the expected string matches the {@link String} argument of {@link Argument#apply(String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the {@link Argument#apply(String)} method of the current {@link Argument} instance is called.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the non-null value to return if the expected string matches
     * @return the new {@link Argument}
     */
    default Argument<T> prefilter(final String expected, final T fixedValue) {
        return string -> expected.equalsIgnoreCase(string) ? fixedValue : apply(string);
    }

    /**
     * Returns a new {@link Argument} that checks for the given expected string before
     * applying the {@link Argument} this method is called on.
     * If the expected string matches the {@link String} argument of {@link Argument#apply(String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the {@link Argument#apply(String)} method of the current {@link Argument} instance is called.
     * Since Argument#apply(String) must not return null, this method returns an {@link Optional} of the result.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the nullable value to return if the expected string matches
     * @return the new {@link Argument}
     */
    default Argument<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return string -> Optional.ofNullable(expected.equalsIgnoreCase(string) ? fixedValue : apply(string));
    }
}
