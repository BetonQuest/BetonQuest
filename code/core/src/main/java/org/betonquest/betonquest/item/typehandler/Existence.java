package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.jspecify.annotations.Nullable;

/**
 * The existence of Item parts.
 */
public enum Existence {
    /**
     * Must be present.
     */
    REQUIRED,
    /**
     * Not allowed.
     */
    FORBIDDEN,
    /**
     * Not relevant.
     */
    WHATEVER;

    /**
     * Value forbidding the existence of a value.
     */
    public static final String NONE_KEY = "none";

    public static <T> Argument<Pair<Existence, @Nullable T>> whateverNullValue() {
        return new DefaultArgument<>(Pair.of(WHATEVER, null));
    }

    public static <T> DecoratableChainRetriever<Pair<Existence, @Nullable T>> apply(
            final DecoratableChainRetriever<T> retriever) {
        return retriever
                .map(value -> Pair.of(Existence.REQUIRED, value))
                .prefilter(Existence.NONE_KEY, Pair.of(FORBIDDEN, null));
    }

    public static <T> Argument<Pair<Existence, @Nullable T>> apply(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return apply(retriever)
                .get(key, Pair.of(WHATEVER, null));
    }
}
