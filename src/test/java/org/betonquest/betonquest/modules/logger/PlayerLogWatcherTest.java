package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.modules.logger.handler.chat.ReceiverSelectorRegistry;
import org.betonquest.betonquest.modules.logger.handler.chat.RecordReceiverSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link PlayerLogWatcher}.
 */
@ExtendWith(MockitoExtension.class)
class PlayerLogWatcherTest {

    /**
     * Test pattern.
     */
    private static final String PATTERN = "test-pattern";

    @Test
    void testNewLogWatcherHasNoFilters(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertFalse(watcher.hasActiveFilters(uuid), "PlayerLogWatcher should not have active filters");
    }

    @Test
    void testNewLogWatcherReturnsEmptySetForActivePatterns(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertEquals(Collections.emptySet(), watcher.getActivePatterns(uuid), "PlayerLogWatcher should have no active patterns");
    }

    @Test
    void testInNewLogWatcherNoPatternIsActive(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertFalse(watcher.isActivePattern(uuid, PATTERN), "No pattern should be reported as active from PlayerLogWatcher");
    }

    @Test
    void testRemovingAnNeverRegisteredFilterIsSilentlyIgnored(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertDoesNotThrow(() -> watcher.removeFilter(uuid, PATTERN), "Removing an pattern that wasn't registered should not throw any exception");
    }

    @Test
    void testHasActiveFilterAfterRegisteringOne(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);

        assertTrue(watcher.hasActiveFilters(uuid), "After registering a filter the PlayerLogWatcher should report active filters for that UUID");
    }

    @Test
    void testHasNoActiveFilterAfterRegisteringOneForAnotherUser(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        watcher.addFilter(UUID.randomUUID(), PATTERN, Level.INFO);
        assertFalse(watcher.hasActiveFilters(UUID.randomUUID()), "After registering a filter the PlayerLogWatcher should report no active filters for any other UUID");
    }

    @Test
    void testAddFilterRegistersASelector(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);

        verify(registry).addSelector(any());
    }

    @Test
    void testRemoveFilterUnregistersTheSelectorAgain(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);
        watcher.removeFilter(uuid, PATTERN);

        verify(registry).removeSelector(any());
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testOverwritingAFilterUnregistersTheOldSelector(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);
        final ArgumentCaptor<RecordReceiverSelector> argument = ArgumentCaptor.forClass(RecordReceiverSelector.class);
        verify(registry).addSelector(argument.capture());

        watcher.addFilter(uuid, PATTERN, Level.WARNING);

        verify(registry).removeSelector(argument.getValue());
        verify(registry, times(2)).addSelector(any());
    }
}
