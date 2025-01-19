package org.betonquest.betonquest.logger.handler.chat;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link AccumulatingReceiverSelector}.
 */
class AccumulatingReceiverSelectorTest {

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testAccumulatingReceiverSelector() {
        final UUID uuid = UUID.randomUUID();

        final AccumulatingReceiverSelector receiverSelector = new AccumulatingReceiverSelector();
        final RecordReceiverSelector selector = mock(RecordReceiverSelector.class);
        when(selector.findReceivers(any())).thenReturn(Set.of(uuid));

        assertEquals(0, receiverSelector.findReceivers(null).size(), "There should be no receivers");
        receiverSelector.addSelector(selector);
        final Set<UUID> receivers = receiverSelector.findReceivers(null);
        assertEquals(1, receivers.size(), "There should be exactly one receiver");
        assertEquals(uuid, receivers.iterator().next(), "The uuid does not match the expected uuid");
        receiverSelector.removeSelector(selector);
        assertEquals(0, receiverSelector.findReceivers(null).size(), "There should be no receivers");
    }
}
