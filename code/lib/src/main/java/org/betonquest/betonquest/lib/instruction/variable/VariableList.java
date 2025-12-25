package org.betonquest.betonquest.lib.instruction.variable;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.ValueChecker;
import org.betonquest.betonquest.api.instruction.variable.ValueParser;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A Variable that resolves into a list of {@link T}.
 *
 * @param <T> the variable type
 */
public class VariableList<T> extends DefaultVariable<List<T>> {

    /**
     * Creates a new VariableList.
     *
     * @param value the list of values
     */
    public VariableList(final List<T> value) {
        super(value);
    }

    /**
     * Creates a new VariableList.
     *
     * @param values the list of values
     */
    @SafeVarargs
    public VariableList(final T... values) {
        super(List.of(values));
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     * Any constant part will be validated in construction.
     *
     * @param variables the variable processor to create and resolve variables
     * @param pack      the package in which the variable is used in
     * @param input     the string that may contain variables
     * @param resolver  the resolver to convert the resolved variable to the given type
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableList(final Variables variables, @Nullable final QuestPackage pack, final String input,
                        final ValueParser<T> resolver) throws QuestException {
        this(variables, pack, input, resolver, (value) -> {
        });
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     * Any constant part will be validated in construction.
     *
     * @param variables    the variable processor to create and resolve variables
     * @param pack         the package in which the variable is used in
     * @param input        the string that may contain variables
     * @param resolver     the resolver to convert the resolved variable to the given type
     * @param valueChecker the checker to verify valid lists
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableList(final Variables variables, @Nullable final QuestPackage pack, final String input,
                        final ValueParser<T> resolver, final ValueChecker<List<T>> valueChecker) throws QuestException {
        this(variables, pack, input, new MarkedResolver<>(new ValueParser<>() {
            @Override
            public List<T> apply(final String value) throws QuestException {
                final List<T> list = new ArrayList<>();
                for (final String part : StringUtils.split(value, ',')) {
                    list.add(resolver.apply(part));
                }
                valueChecker.check(list);
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

    private VariableList(final Variables variables, @Nullable final QuestPackage pack, final String input,
                         final MarkedResolver<T> resolver) throws QuestException {
        super(variables, pack, input, resolver);
        if (!resolver.called) {
            for (final String unresolved : StringUtils.split(input, ',')) {
                new DefaultVariable<>(variables, pack, unresolved, resolver);
            }
        }
    }

    /**
     * {@link ValueChecker} for Lists that must not be empty.
     *
     * @param <T> the type of the list
     * @return the value checker
     */
    public static <T> ValueChecker<List<T>> notEmptyChecker() {
        return (value) -> {
            if (value.isEmpty()) {
                throw new QuestException("List must not be empty");
            }
        };
    }

    /**
     * Checks if the resulting list does not have a duplicate key.
     *
     * @param <T> the key type to check for duplicates
     * @param <U> the value type
     * @return the value checker
     */
    public static <T, U> ValueChecker<List<Map.Entry<T, U>>> notDuplicateKeyChecker() {
        return value -> {
            final List<T> keys = new ArrayList<>();
            for (final Map.Entry<T, U> entry : value) {
                if (keys.contains(entry.getKey())) {
                    throw new QuestException("List does not allow duplicate keys: " + entry.getKey());
                }
                keys.add(entry.getKey());
            }
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
