package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The default implementation for {@link FlagArgument} using {@link DefaultArgument}.
 *
 * @param <T> the type of the flag
 */
public class DefaultFlagArgument<T> extends DefaultArgument<Optional<T>> implements FlagArgument<T> {

    /**
     * The state of the flag.
     */
    private final FlagState state;

    /**
     * Constructor creating a flag argument using a fixed value.
     *
     * @param value the fixed value
     * @param state the flag's state
     */
    public DefaultFlagArgument(final T value, final FlagState state) {
        super(Optional.of(value));
        this.state = state;
    }

    /**
     * Constructor creating an {@link FlagState#ABSENT} flag argument.
     */
    public DefaultFlagArgument() {
        super(Optional.empty());
        this.state = FlagState.ABSENT;
    }

    /**
     * Constructor creating a {@link FlagState#DEFINED} flag argument using an input and parser.
     *
     * @param placeholders the {@link PlaceholderManager} to create and resolve placeholders
     * @param pack         the related package
     * @param input        the raw argument value
     * @param valueParser  the parser
     * @throws QuestException if an error occurs on creation
     */
    public DefaultFlagArgument(final PlaceholderManager placeholders, @Nullable final QuestPackage pack, final String input,
                               final ValueParser<Optional<T>> valueParser) throws QuestException {
        super(placeholders, pack, input, valueParser);
        this.state = FlagState.DEFINED;
    }

    /**
     * Constructor creating a {@link FlagState#DEFINED} flag argument using an input and parser.
     *
     * @param placeholders the {@link PlaceholderManager} to create and resolve placeholders
     * @param pack         the related package
     * @param input        the raw argument value
     * @param valueParser  the parser
     * @param cache        whether to store the result of the first successful apply and use that same object
     * @throws QuestException if an error occurs on creation
     */
    public DefaultFlagArgument(final PlaceholderManager placeholders, @Nullable final QuestPackage pack, final String input,
                               final ValueParser<Optional<T>> valueParser, final boolean cache) throws QuestException {
        super(placeholders, pack, input, valueParser, true, cache);
        this.state = FlagState.DEFINED;
    }

    @Override
    public FlagState getState() {
        return state;
    }
}
