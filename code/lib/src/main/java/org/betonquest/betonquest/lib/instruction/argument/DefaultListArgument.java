package org.betonquest.betonquest.lib.instruction.argument;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link DefaultArgument} that resolves to a list of {@link T}.
 *
 * @param <T> the type of the list elements
 */
public class DefaultListArgument<T> extends DefaultArgument<List<T>> {

    /**
     * Creates a new {@link DefaultListArgument}.
     *
     * @param value the list of values
     */
    public DefaultListArgument(final List<T> value) {
        super(value);
    }

    /**
     * Creates a new {@link DefaultListArgument}.
     *
     * @param values the list of values
     */
    @SafeVarargs
    public DefaultListArgument(final T... values) {
        super(List.of(values));
    }

    /**
     * Resolves a string that may contain placeholders to a {@link DefaultListArgument} of the given type.
     * Any constant part will be validated in construction.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param pack         the package of the instruction in which the argument is used
     * @param input        the string that may contain placeholders
     * @param resolver     the resolver parsing a string to a value of type T
     * @throws QuestException if the placeholders could not be created or resolved to the given type
     */
    public DefaultListArgument(final Placeholders placeholders, @Nullable final QuestPackage pack, final String input,
                               final ValueParser<T> resolver) throws QuestException {
        this(placeholders, pack, input, resolver, value -> true);
    }

    /**
     * Resolves a string that may contain placeholders to a {@link DefaultListArgument} of the given type.
     * Any constant part will be validated in construction.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param pack         the package of the instruction in which the argument is used
     * @param input        the string that may contain placeholders
     * @param resolver     the resolver parsing a string to a value of type T
     * @param valueChecker the checker to verify valid lists
     * @throws QuestException if the placeholders could not be created or resolved to the given type
     */
    public DefaultListArgument(final Placeholders placeholders, @Nullable final QuestPackage pack, final String input,
                               final ValueParser<T> resolver, final ValueValidator<List<T>> valueChecker) throws QuestException {
        this(placeholders, pack, input, new MarkedResolver<>(new ValueParser<>() {
            @Override
            public List<T> apply(final String value) throws QuestException {
                final List<T> list = new ArrayList<>();
                for (final String part : StringUtils.split(value, ',')) {
                    list.add(resolver.apply(part));
                }
                if (!valueChecker.validate(list)) {
                    throw new QuestException("Invalid value: " + value);
                }
                return list;
            }

            @Override
            public List<T> cloneValue(final List<T> value) {
                final List<T> list = new ArrayList<>();
                for (final T part : value) {
                    list.add(resolver.cloneValue(part));
                }
                return list;
            }
        }));
    }

    private DefaultListArgument(final Placeholders placeholders, @Nullable final QuestPackage pack, final String input,
                                final MarkedResolver<T> resolver) throws QuestException {
        super(placeholders, pack, input, resolver);
        if (!resolver.called) {
            for (final String unresolved : StringUtils.split(input, ',')) {
                new DefaultArgument<>(placeholders, pack, unresolved, resolver);
            }
        }
    }

    /**
     * {@link ValueValidator} for Lists that must not be empty.
     *
     * @param <T> the type of the list
     * @return the value checker
     */
    public static <T> ValueValidator<List<T>> notEmptyChecker() {
        return (value) -> {
            if (value.isEmpty()) {
                throw new QuestException("List must not be empty");
            }
            return true;
        };
    }

    /**
     * A Resolver Decorator to check if a resolver has been applied.
     *
     * @param <T> the type to resolve to
     */
    private static final class MarkedResolver<T> implements ValueParser<List<T>> {

        /**
         * The resolver which applying should be marked.
         */
        private final ValueParser<List<T>> resolver;

        /**
         * If the resolver was applied.
         */
        private boolean called;

        private MarkedResolver(final ValueParser<List<T>> resolver) {
            this.resolver = resolver;
            this.called = false;
        }

        @Override
        public List<T> apply(final String arg) throws QuestException {
            called = true;
            return resolver.apply(arg);
        }

        @Override
        public List<T> cloneValue(final List<T> value) {
            return resolver.cloneValue(value);
        }
    }
}
