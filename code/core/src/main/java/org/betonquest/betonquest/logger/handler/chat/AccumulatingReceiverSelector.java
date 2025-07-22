package org.betonquest.betonquest.logger.handler.chat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.LogRecord;

/**
 * A {@link RecordReceiverSelector} that selects all receivers that its underlying selectors match, combining the
 * results. It implements {@link ReceiverSelectorRegistry} to allow controlling the selectors that are used.
 * <p>
 * Due to its thread-safe mutability it works best with small amounts of selectors.
 */
public class AccumulatingReceiverSelector implements RecordReceiverSelector, ReceiverSelectorRegistry {

    /**
     * Child selectors to use for accumulating matches.
     */
    private final Set<RecordReceiverSelector> partialSelectors;

    /**
     * Create the accumulating selector in an empty state.
     */
    public AccumulatingReceiverSelector() {
        partialSelectors = new CopyOnWriteArraySet<>();
    }

    @Override
    public Set<UUID> findReceivers(final LogRecord record) {
        final Set<UUID> matches = new HashSet<>();
        for (final RecordReceiverSelector selector : partialSelectors) {
            matches.addAll(selector.findReceivers(record));
        }
        return matches;
    }

    @Override
    public void addSelector(final RecordReceiverSelector selector) {
        partialSelectors.add(selector);
    }

    @Override
    public void removeSelector(final RecordReceiverSelector selector) {
        partialSelectors.remove(selector);
    }
}
