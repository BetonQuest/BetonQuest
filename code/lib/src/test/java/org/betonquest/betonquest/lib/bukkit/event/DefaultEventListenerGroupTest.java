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

/**
 * Test {@link DefaultEventListenerGroup}.
 */
@ExtendWith(MockitoExtension.class)
class DefaultEventListenerGroupTest {

    /**
     * A mocked logger.
     */
    @Mock
    private BetonQuestLogger logger;

    /**
     * A mocked {@link DefaultEventListenerGroup} to spy on.
     */
    private DefaultEventListenerGroup<PlayerJumpEvent> group;

    /**
     * Creates a mocked {@link ProxyListener} for the given type and priority.
     * May fail if the {@link org.bukkit.event.HandlerList} could not be extracted from the event class.
     *
     * @param type     the event type
     * @param priority the priority for the event listener
     * @param <T>      the event type
     * @return the mocked listener
     * @throws QuestException if an error occurs while creating the listener
     */
    private <T extends Event> ProxyListener<T> mockedListener(final Class<T> type, final EventPriority priority)
            throws QuestException {
        return spy(new DefaultProxyListener<>(type, new RegisteredListener(new Listener() {
        }, (l, e) -> {
        }, priority, mock(Plugin.class), false)));
    }

    /**
     * Verify states of registered listeners.
     *
     * @param listeners          the listeners to verify
     * @param expectedRegistered the expected registered priorities to be registered
     */
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

    /**
     * Generates listeners for the given group.
     *
     * @param listeners The map of listeners to fill.
     * @param group     The group to generate listeners for and attach to
     * @throws QuestException If an error occurs while creating the listeners.
     */
    private void generateListeners(final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners,
                                   final DefaultEventListenerGroup<PlayerJumpEvent> group) throws QuestException {
        for (final EventPriority priority : EventPriority.values()) {
            final ProxyListener<PlayerJumpEvent> listener = mockedListener(PlayerJumpEvent.class, priority);
            listeners.put(priority, listener);
            doReturn(listener).when(group).createListener(any(), eq(PlayerJumpEvent.class), eq(priority));
        }
    }

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setup() {
        group = spy(new DefaultEventListenerGroup<>(logger, PlayerJumpEvent.class));
    }

    /**
     * Test {@link DefaultEventListenerGroup#bake(Plugin)}.
     *
     * @param plugin mocked plugin
     * @throws QuestException if an error occurs while baking
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testBake(@Mock final Plugin plugin) throws QuestException {
        doReturn(mock(ProxyListener.class)).when(group).createListener(any(), any(), any());
        for (final EventPriority priority : EventPriority.values()) {
            assertThrows(IllegalStateException.class, () -> group.getListener(priority), "Listener should not be available yet");
        }
        group.bake(plugin);
        verify(group, times(EventPriority.values().length)).createListener(eq(plugin), eq(PlayerJumpEvent.class), any());
        for (final EventPriority priority : EventPriority.values()) {
            assertDoesNotThrow(() -> group.getListener(priority), "Listener should be available now");
        }
    }

    /**
     * Test {@link DefaultEventListenerGroup#unsubscribe(EventPriority, EventServiceSubscriber)}.
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testUnsubscribe() throws QuestException {
        group.bake(mock(Plugin.class));
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

    /**
     * Test {@link DefaultEventListenerGroup#subscribe(EventPriority, boolean, EventServiceSubscriber)}
     * and {@link DefaultEventListenerGroup#callEvent(Event, EventPriority)}.
     * Also tests the ignoreCancelled flag.
     */
    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testSubscribeAndCall() throws QuestException {
        group.bake(mock(Plugin.class));
        final EventServiceSubscriber<PlayerJumpEvent> subscriber = (event, priority) -> {
            throw new QuestException(event.getEventName() + "/" + priority);
        };
        final EventServiceSubscriber<PlayerJumpEvent> resultSubscriber = group.subscribe(EventPriority.NORMAL, false, subscriber);
        final EventServiceSubscriber<PlayerJumpEvent> resultSubscriber2 = group.subscribe(EventPriority.HIGH, true, subscriber);
        assertNotSame(resultSubscriber, resultSubscriber2, "Subscribers should not be the same");

        final PlayerJumpEvent jumpEvent = mock(PlayerJumpEvent.class);
        final String eventName = "PlayerJumpEvent";
        when(jumpEvent.getEventName()).thenReturn(eventName);
        when(jumpEvent.isCancelled()).thenReturn(true);
        assertThrows(QuestException.class, () -> group.callEvent(jumpEvent, EventPriority.NORMAL),
                "callEvent should throw when called");
        assertDoesNotThrow(() -> group.callEvent(jumpEvent, EventPriority.HIGH),
                "callEvent should not throw when called");
    }

    /**
     * Test {@link DefaultEventListenerGroup#require(EventPriority)}
     * and {@link DefaultEventListenerGroup#disable(EventPriority)}.
     *
     * @throws QuestException if an error occurs while creating the listeners.
     */
    @Test
    @SuppressWarnings({"PMD.LooseCoupling", "PMD.UnitTestContainsTooManyAsserts"})
    void testRequireAndDisabling() throws QuestException {
        final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners = new EnumMap<>(EventPriority.class);
        generateListeners(listeners, group);

        assertDoesNotThrow(() -> group.bake(mock(Plugin.class)), "Baking event listener group should not fail");
        verifyRegistrationStates(listeners, Set.of());

        group.require(EventPriority.LOWEST);
        group.require(EventPriority.NORMAL);
        verifyRegistrationStates(listeners, Set.of(EventPriority.LOWEST, EventPriority.NORMAL));
        verify(listeners.get(EventPriority.LOWEST), times(1)).register();
        verify(listeners.get(EventPriority.NORMAL), times(1)).register();

        group.require(EventPriority.LOWEST);
        group.require(EventPriority.HIGH);
        verifyRegistrationStates(listeners, Set.of(EventPriority.LOWEST, EventPriority.NORMAL, EventPriority.HIGH));
        verify(listeners.get(EventPriority.LOWEST), times(1)).register();
        verify(listeners.get(EventPriority.HIGH), times(1)).register();

        for (final EventPriority priority : EventPriority.values()) {
            for (int i = 0; i < 100; i++) {
                group.require(priority);
            }
        }

        verifyRegistrationStates(listeners, EnumSet.allOf(EventPriority.class));
        listeners.values().forEach(listener -> verify(listener, times(1)).register());

        final EnumSet<EventPriority> toDisable = EnumSet.of(EventPriority.LOWEST, EventPriority.LOW, EventPriority.NORMAL, EventPriority.MONITOR);
        final Set<EventPriority> stillEnabled = EnumSet.complementOf(toDisable);
        toDisable.forEach(group::disable);

        verifyRegistrationStates(listeners, stillEnabled);
        toDisable.forEach(p -> verify(listeners.get(p), times(1)).unregister());
        stillEnabled.forEach(p -> verify(listeners.get(p), never()).unregister());

        final EnumSet<EventPriority> toEnable = EnumSet.of(EventPriority.LOW, EventPriority.NORMAL);
        final EnumSet<EventPriority> stillDisabled = EnumSet.of(EventPriority.LOWEST, EventPriority.MONITOR);
        toEnable.forEach(group::require);

        verifyRegistrationStates(listeners, EnumSet.complementOf(stillDisabled));

        toEnable.forEach(p -> verify(listeners.get(p), times(2)).register());
        stillDisabled.forEach(p -> verify(listeners.get(p), times(1)).register());
    }
}
