package org.betonquest.betonquest.lib.bukkit.event;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultBukkitEventServiceTest {

    @Mock
    private Plugin plugin;

    @Mock
    private BetonQuestLoggerFactory loggerFactory;

    private DefaultBukkitEventService service;

    private BetonQuestLogger logger;

    private EventListenerGroup<PlayerJumpEvent> group;

    private static <T extends Event> EventServiceSubscriber<T> empty() {
        return (e, p) -> {
        };
    }

    @BeforeEach
    void setup() throws QuestException {
        logger = mock(BetonQuestLogger.class);
        when(loggerFactory.create(plugin, "EventService")).thenReturn(logger);
        service = new DefaultBukkitEventService(plugin, loggerFactory);
        group = spy(service.require(PlayerJumpEvent.class).orElseThrow());
        group.bake(plugin);
        service = spy(service);
        lenient().doReturn(Optional.of(group)).when(service).require(PlayerJumpEvent.class);
        lenient().doCallRealMethod().when(service).require(Event.class);
    }

    @Test
    void require_successful() {
        assertTrue(service.require(PlayerJumpEvent.class, EventPriority.NORMAL), "Should be able to require an event");
    }

    @Test
    void require_call_on_service_with_event() {
        service.require(PlayerJumpEvent.class, EventPriority.NORMAL);
        verify(service, times(1)).require(PlayerJumpEvent.class);
    }

    @Test
    void require_call_on_group_with_priority() {
        service.require(PlayerJumpEvent.class, EventPriority.NORMAL);
        verify(group, times(1)).require(EventPriority.NORMAL);
    }

    @Test
    void require_failed_successfully() {
        assertFalse(service.require(Event.class, EventPriority.NORMAL), "Should not be able to require an event");
    }

    @Test
    void require_failed_call_on_service_with_event() {
        service.require(Event.class, EventPriority.NORMAL);
        verify(service, times(1)).require(Event.class);
    }

    @Test
    void require_failed_call_on_logger_with_error() {
        service.require(Event.class, EventPriority.NORMAL);
        verify(logger, times(1)).error(anyString(), any());
    }

    @Test
    void require_failed_without_call_on_group() {
        service.require(Event.class, EventPriority.NORMAL);
        verify(group, never()).require(any());
    }

    @Test
    void subscribe_successful_without_exception() {
        final EventServiceSubscriber<PlayerJumpEvent> empty = empty();
        assertDoesNotThrow(() -> service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty), "Subscriber should not be null");
    }

    @Test
    void subscribe_successful_returning_not_null() throws QuestException {
        final EventServiceSubscriber<PlayerJumpEvent> empty = empty();
        final EventServiceSubscriber<PlayerJumpEvent> subscriber = service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty);
        assertNotNull(subscriber, "Subscriber should not be null");
    }

    @Test
    void subscribe_successful_call_on_service_with_event() throws QuestException {
        final EventServiceSubscriber<PlayerJumpEvent> empty = empty();
        service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty);
        verify(service, times(1)).require(PlayerJumpEvent.class);
    }

    @Test
    void subscribe_successful_call_on_group_with_subscribe() throws QuestException {
        final EventServiceSubscriber<PlayerJumpEvent> empty = empty();
        service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty);
        verify(group, times(1)).subscribe(eq(EventPriority.NORMAL), eq(true), eq(empty));
    }

    @Test
    void subscribe_failed_successfully_with_require_call() {
        assertThrows(QuestException.class, () -> service.subscribe(Event.class, EventPriority.NORMAL, empty()),
                "Should not be able to subscribe to an event");
        verify(service, times(1)).require(Event.class);
    }

    @Test
    void subscribe_failed_successfully_with_subscribe_call() {
        assertThrows(QuestException.class, () -> service.subscribe(Event.class, EventPriority.NORMAL, empty()),
                "Should not be able to subscribe to an event");
        verify(group, never()).subscribe(any(), anyBoolean(), any());
    }

    @Test
    void unsubscribe_successful_without_exception() {
        final EventServiceSubscriber<PlayerJumpEvent> subscriber = assertDoesNotThrow(() ->
                service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty()), "Subscriber should not be null");
        assertDoesNotThrow(() -> service.unsubscribe(PlayerJumpEvent.class, EventPriority.NORMAL, subscriber), "Unsubscribe should not throw");
    }

    @Test
    void unsubscribe_successful_without_call_on_service_require() throws QuestException {
        final EventServiceSubscriber<PlayerJumpEvent> subscriber = service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty());
        service.unsubscribe(PlayerJumpEvent.class, EventPriority.NORMAL, subscriber);
        verify(service, times(2)).require(eq(PlayerJumpEvent.class));
    }

    @Test
    void unsubscribe_successful_without_call_on_group_unsubscribe() throws QuestException {
        final EventServiceSubscriber<PlayerJumpEvent> subscriber = service.subscribe(PlayerJumpEvent.class, EventPriority.NORMAL, empty());
        service.unsubscribe(PlayerJumpEvent.class, EventPriority.NORMAL, subscriber);
        verify(group, times(1)).unsubscribe(EventPriority.NORMAL, subscriber);
    }

    @Test
    void unsubscribe_failed_successfully_without_call_on_group_unsubscribe() {
        assertDoesNotThrow(() -> service.unsubscribe(Event.class, EventPriority.NORMAL, empty()), "Unsubscribe should not throw");
        verify(group, never()).unsubscribe(EventPriority.NORMAL, empty());
    }
}
