package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.ValueChecker;
import org.betonquest.betonquest.instruction.types.BlockSelectorParser;
import org.betonquest.betonquest.instruction.types.BooleanParser;
import org.betonquest.betonquest.instruction.types.EnumParser;
import org.betonquest.betonquest.instruction.types.NumberParser;
import org.betonquest.betonquest.instruction.types.StringParser;
import org.betonquest.betonquest.instruction.types.location.LocationParser;
import org.betonquest.betonquest.instruction.types.location.VectorParser;
import org.betonquest.betonquest.instruction.types.location.WorldParser;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.bukkit.Bukkit;

import java.util.List;

/**
 * Objectified parser for the Instruction to get a {@link T} from string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface Argument<T> extends QuestFunction<String, T> {
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
     * Gets a list of {@link T}s from string.
     *
     * @param argument the argument to parse
     * @param <T>      what the argument returns
     * @return the list of {@link T}s
     */
    static <T> VariableArgument<VariableList<T>> ofList(final Argument<T> argument) {
        return ofList(argument, list -> {
        });
    }

    /**
     * Gets a list of {@link T}s from string.
     *
     * @param argument the argument to parse
     * @param checker  the checker to validate the list
     * @param <T>      what the argument returns
     * @return the list of {@link T}s
     */
    static <T> VariableArgument<VariableList<T>> ofList(final Argument<T> argument, final ValueChecker<List<T>> checker) {
        return (variableProcessor, pack, string)
                -> new VariableList<>(variableProcessor, pack, string, argument, checker);
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
}
