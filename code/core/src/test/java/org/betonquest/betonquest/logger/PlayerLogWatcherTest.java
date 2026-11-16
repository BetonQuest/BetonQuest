package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.logger.handler.chat.ReceiverSelectorRegistry;
import org.betonquest.betonquest.logger.handler.chat.RecordReceiverSelector;
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
    void new_log_watcher_has_no_filters(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertFalse(watcher.hasActiveFilters(uuid), "PlayerLogWatcher should not have active filters");
    }

    @Test
    void new_log_watcher_returns_empty_set_for_active_patterns(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertEquals(Collections.emptySet(), watcher.getActivePatterns(uuid), "PlayerLogWatcher should have no active patterns");
    }

    @Test
    void in_new_log_watcher_no_pattern_is_active(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertFalse(watcher.isActivePattern(uuid, PATTERN), "No pattern should be reported as active from PlayerLogWatcher");
    }

    @Test
    void removing_an_never_registered_filter_is_silently_ignored(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();
        assertDoesNotThrow(() -> watcher.removeFilter(uuid, PATTERN), "Removing an pattern that wasn't registered should not throw any exception");
    }

    @Test
    void has_active_filter_after_registering_one(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);

        assertTrue(watcher.hasActiveFilters(uuid), "After registering a filter the PlayerLogWatcher should report active filters for that UUID");
    }

    @Test
    void has_no_active_filter_after_registering_one_for_another_user(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        watcher.addFilter(UUID.randomUUID(), PATTERN, Level.INFO);
        assertFalse(watcher.hasActiveFilters(UUID.randomUUID()), "After registering a filter the PlayerLogWatcher should report no active filters for any other UUID");
    }

    @Test
    void add_filter_registers_a_selector(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);

        verify(registry).addSelector(any());
    }

    @Test
    void remove_filter_unregisters_the_selector_again(@Mock final ReceiverSelectorRegistry registry) {
        final PlayerLogWatcher watcher = new PlayerLogWatcher(registry);
        final UUID uuid = UUID.randomUUID();

        watcher.addFilter(uuid, PATTERN, Level.INFO);
        watcher.removeFilter(uuid, PATTERN);

        verify(registry).removeSelector(any());
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void overwriting_a_filter_unregisters_the_old_selector(@Mock final ReceiverSelectorRegistry registry) {
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
