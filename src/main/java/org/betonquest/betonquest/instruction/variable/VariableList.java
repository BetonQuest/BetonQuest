package org.betonquest.betonquest.instruction.variable;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Variable that resolves into a list of {@link T}.
 *
 * @param <T> the variable type
 */
public class VariableList<T> extends Variable<List<T>> {

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     * Any constant part will be validated in construction.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param resolver          the resolver to convert the resolved variable to the given type
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public VariableList(final VariableProcessor variableProcessor, @Nullable final QuestPackage pack, final String input,
                        final QuestFunction<String, T> resolver) throws QuestException {
        this(variableProcessor, pack, input, new MarkedResolver<>(value -> {
            final List<T> list = new ArrayList<>();
            for (final String part : StringUtils.split(value, ',')) {
                list.add(resolver.apply(part));
            }
            return list;
        }));
    }

    private VariableList(final VariableProcessor variableProcessor, @Nullable final QuestPackage pack, final String input,
                         final MarkedResolver<T> resolver) throws QuestException {
        super(variableProcessor, pack, input, resolver);
        if (!resolver.called) {
            for (final String unresolved : StringUtils.split(input, ',')) {
                new Variable<>(variableProcessor, pack, unresolved, resolver);
            }
        }
    }

    /**
     * A Resolver Decorator to check if a resolver has been applied.
     *
     * @param <T> the type to resolve to
     */
    private static final class MarkedResolver<T> implements QuestFunction<String, List<T>> {

        /**
         * The resolver which applying should be marked.
         */
        private final QuestFunction<String, List<T>> resolver;

        /**
         * If the resolver was applied.
         */
        private boolean called;

        private MarkedResolver(final QuestFunction<String, List<T>> resolver) {
            this.resolver = resolver;
            this.called = false;
        }

        @Override
        public List<T> apply(final String arg) throws QuestException {
            called = true;
            return resolver.apply(arg);
        }
    }
}
