package org.betonquest.betonquest.lib.bukkit.event;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.EventServiceSubscriber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultEventListenerGroupTest {

    @Mock
    private static Plugin plugin;

    @Mock
    private BetonQuestLogger logger;

    private DefaultEventListenerGroup<PlayerJumpEvent> group;

    private <T extends Event> ProxyListener<T> mockedListener(final Class<T> type, final EventPriority priority)
            throws QuestException {
        return spy(new DefaultProxyListener<>(type, new RegisteredListener(new Listener() {
        }, (l, e) -> {
        }, priority, mock(Plugin.class), false)));
    }

    private void verifyRegistrationStates(final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners,
                                          final Set<EventPriority> expectedRegistered) {
        listeners.forEach((priority, listener) -> {
            if (expectedRegistered.contains(priority)) {
                assertTrue(listener.isRegistered(), "Expected " + priority + " to be registered");
            } else {
                assertFalse(listener.isRegistered(), "Expected " + priority + " not to be registered");
            }
        });
    }

    private void generateListeners(final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners,
                                   final DefaultEventListenerGroup<PlayerJumpEvent> group) throws QuestException {
        for (final EventPriority priority : EventPriority.values()) {
            final ProxyListener<PlayerJumpEvent> listener = mockedListener(PlayerJumpEvent.class, priority);
            listeners.put(priority, listener);
            doReturn(listener).when(group).createListener(any(), eq(PlayerJumpEvent.class), eq(priority));
        }
    }

    @BeforeEach
    void setup() {
        group = spy(new DefaultEventListenerGroup<>(logger, PlayerJumpEvent.class));
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testBake() throws QuestException {
        doReturn(mock(ProxyListener.class)).when(group).createListener(any(), any(), any());
        for (final EventPriority priority : EventPriority.values()) {
            assertThrows(IllegalStateException.class, () -> group.getListener(priority), "Listener should not be available yet");
        }
        group.bake(plugin);
        verify(group, times(EventPriority.values().length)).createListener(eq(plugin), eq(PlayerJumpEvent.class), any());
        for (final EventPriority priority : EventPriority.values()) {
            assertNotNull(group.getListener(priority), "Listener should be available now");
        }
    }

    @Test
    void noBakeSubscribe() {
        assertThrows(IllegalStateException.class,
                () -> group.subscribe(EventPriority.NORMAL, false, (event, priority) -> {
                }), "Cannot subscribe to event before baking");
    }

    @Test
    void noBakeRequire() {
        assertFalse(group.require(EventPriority.NORMAL), "Should not be able to require event before baking");
        verify(logger, times(1)).error(anyString());
    }

    @Test
    void noBakeDisable() {
        group.disable(EventPriority.NORMAL);
        verify(logger, times(1)).error(anyString());
    }

    @Nested
    class Baked {

        @BeforeEach
        void setup() throws QuestException {
            group.bake(plugin);
        }

        @Test
        @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
        void testUnsubscribe() {
            final EventServiceSubscriber<PlayerJumpEvent> subscription = group.subscribe(EventPriority.NORMAL, false,
                    (event, priority) -> {
                    });
            assertNotNull(subscription, "Subscription should not be null");
            group.unsubscribe(EventPriority.HIGH, subscription);
            verify(logger, times(1)).warn(anyString());
            group.unsubscribe(EventPriority.NORMAL, subscription);
            verify(logger, times(1)).warn(anyString());
            group.unsubscribe(EventPriority.NORMAL, subscription);
            verify(logger, times(2)).warn(anyString());
        }

        @Test
        void testSubscribe() {
            final EventServiceSubscriber<PlayerJumpEvent> subscriber = (event, priority) -> {
                throw new QuestException(event.getEventName() + "/" + priority);
            };
            final EventServiceSubscriber<PlayerJumpEvent> resultSubscriber = group.subscribe(EventPriority.NORMAL, false, subscriber);
            final EventServiceSubscriber<PlayerJumpEvent> resultSubscriber2 = group.subscribe(EventPriority.NORMAL, true, subscriber);
            assertNotNull(resultSubscriber, "Subscriber should not be null");
            assertNotSame(resultSubscriber, resultSubscriber2, "Subscribers should not be the same");
        }

        @Test
        void testCall() {
            final EventServiceSubscriber<PlayerJumpEvent> subscriber = (event, priority) -> {
                throw new QuestException("");
            };
            group.subscribe(EventPriority.NORMAL, false, subscriber);

            final PlayerJumpEvent jumpEvent = mock(PlayerJumpEvent.class);
            assertThrows(QuestException.class, () -> group.callEvent(jumpEvent, EventPriority.NORMAL),
                    "callEvent should throw when called");
        }

        @Test
        void testIgnoreCancelledFlag() {
            final EventServiceSubscriber<PlayerJumpEvent> subscriber = (event, priority) -> {
                throw new QuestException("");
            };
            group.subscribe(EventPriority.NORMAL, true, subscriber);

            final PlayerJumpEvent jumpEvent = mock(PlayerJumpEvent.class);
            assertThrows(QuestException.class, () -> group.callEvent(jumpEvent, EventPriority.NORMAL),
                    "callEvent should throw when called");
            when(jumpEvent.isCancelled()).thenReturn(true);
            assertDoesNotThrow(() -> group.callEvent(jumpEvent, EventPriority.NORMAL),
                    "callEvent should not throw when called");
        }

        @Nested
        class Requirements {

            private Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners;

            @BeforeEach
            void setup() throws QuestException {
                listeners = new EnumMap<>(EventPriority.class);
                generateListeners(listeners, group);
                group.bake(plugin);
            }

            @Test
            @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
            void testRequire() {
                verifyRegistrationStates(listeners, Set.of());
                group.require(EventPriority.LOWEST);
                verifyRegistrationStates(listeners, Set.of(EventPriority.LOWEST));
                verify(listeners.get(EventPriority.LOWEST), times(1)).register();
            }

            @Test
            void require_a_lot() {
                for (final EventPriority priority : EventPriority.values()) {
                    for (int i = 0; i < 100; i++) {
                        group.require(priority);
                    }
                    verify(listeners.get(priority), times(1)).register();
                }
            }

            @Test
            @SuppressWarnings("PMD.LooseCoupling")
            void disabling() {
                EnumSet.allOf(EventPriority.class).forEach(group::require);

                final EnumSet<EventPriority> toDisable = EnumSet.of(EventPriority.LOWEST, EventPriority.LOW, EventPriority.NORMAL, EventPriority.MONITOR);
                final Set<EventPriority> stillEnabled = EnumSet.complementOf(toDisable);
                toDisable.forEach(group::disable);

                verifyRegistrationStates(listeners, stillEnabled);
                toDisable.forEach(p -> verify(listeners.get(p), times(1)).unregister());
                stillEnabled.forEach(p -> verify(listeners.get(p), never()).unregister());
            }
        }
    }
}
