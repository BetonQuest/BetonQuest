package org.betonquest.betonquest.modules.logger.handler;

/**
 * A registry for {@link RecordReceiverSelector}s.
 */
public interface ReceiverSelectorRegistry {

    /**
     * Add a selector to be taken into account when selecting receivers.
     *
     * @param selector the selector to add
     */
    void addSelector(RecordReceiverSelector selector);

    /**
     * Remove a selector to not be taken into account anymore.
     *
     * @param selector the selector to remove
     */
    void removeSelector(RecordReceiverSelector selector);
}
