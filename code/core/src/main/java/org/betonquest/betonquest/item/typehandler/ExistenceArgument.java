package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.jspecify.annotations.Nullable;

public interface ExistenceArgument<T> extends Argument<Pair<Existence, @Nullable T>> {

    static <T> ExistenceArgument<T> whateverNullValue() {
        return profile -> Pair.of(Existence.WHATEVER, null);
    }

    static <T> DecoratableChainRetriever<Pair<Existence, @Nullable T>> apply(
            final DecoratableChainRetriever<T> retriever) {
        return retriever
                .map(value -> Pair.of(Existence.REQUIRED, value))
                .prefilter(Existence.NONE_KEY, Pair.of(Existence.FORBIDDEN, null));
    }

    static <T> ExistenceArgument<@Nullable T> apply(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return (ExistenceArgument<T>) apply(retriever)
                .get(key, Pair.of(Existence.WHATEVER, null));
    }
}
