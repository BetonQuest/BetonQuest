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
public class DefaultEventListenerGroupTest {

    @Mock
    private BetonQuestLogger logger;

    private DefaultEventListenerGroup<PlayerJumpEvent> group;

    private <T extends Event> ProxyListener<T> mockedListener(final Class<T> type, final EventPriority priority) throws QuestException {
        return spy(new DefaultProxyListener<>(type, new RegisteredListener(new Listener() {
        }, (l, e) -> {
        }, priority, mock(Plugin.class), false)));
    }

    private void verifyRegistrationStates(final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners, final Set<EventPriority> expectedRegistered) {
        listeners.forEach((priority, listener) -> {
            if (expectedRegistered.contains(priority)) {
                assertTrue(listener.isRegistered(), "Expected " + priority + " to be registered");
            } else {
                assertFalse(listener.isRegistered(), "Expected " + priority + " not to be registered");
            }
        });
    }

    private void generateListeners(final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners, final DefaultEventListenerGroup<PlayerJumpEvent> group) throws QuestException {
        for (final EventPriority priority : EventPriority.values()) {
            final ProxyListener<PlayerJumpEvent> listener = mockedListener(PlayerJumpEvent.class, priority);
            listeners.put(priority, listener);
            doReturn(listener).when(group).createListener(any(), eq(PlayerJumpEvent.class), eq(priority));
        }
    }

    @BeforeEach
    public void setup() {
        group = spy(new DefaultEventListenerGroup<>(logger, PlayerJumpEvent.class));
    }

    @Test
    public void testBake(@Mock final Plugin plugin) throws QuestException {
        doReturn(mock(ProxyListener.class)).when(group).createListener(any(), any(), any());
        for (final EventPriority priority : EventPriority.values()) {
            assertThrows(IllegalStateException.class, () -> group.getListener(priority));
        }
        group.bake(plugin);
        verify(group, times(EventPriority.values().length)).createListener(eq(plugin), eq(PlayerJumpEvent.class), any());
        for (final EventPriority priority : EventPriority.values()) {
            assertDoesNotThrow(() -> group.getListener(priority));
        }
    }

    @Test
    public void testUnsubscribe() {
        assertDoesNotThrow(() -> group.bake(mock(Plugin.class)));
        final EventServiceSubscriber<PlayerJumpEvent> subscription = group.subscribe(EventPriority.NORMAL, false,
                (event, priority) -> {
                });
        assertNotNull(subscription);
        group.unsubscribe(EventPriority.HIGH, subscription);
        verify(logger, times(1)).warn(anyString());
        group.unsubscribe(EventPriority.NORMAL, subscription);
        verify(logger, times(1)).warn(anyString());
        group.unsubscribe(EventPriority.NORMAL, subscription);
        verify(logger, times(2)).warn(anyString());
    }

    @Test
    public void testSubscribeAndCall() {
        assertDoesNotThrow(() -> group.bake(mock(Plugin.class)));
        final EventServiceSubscriber<PlayerJumpEvent> subscriber = (event, priority) -> {
            throw new QuestException(event.getEventName() + "/" + priority);
        };
        final EventServiceSubscriber<PlayerJumpEvent> resultSubscriber = group.subscribe(EventPriority.NORMAL, false, subscriber);
        final EventServiceSubscriber<PlayerJumpEvent> resultSubscriber2 = group.subscribe(EventPriority.HIGH, true, subscriber);
        assertNotSame(resultSubscriber, resultSubscriber2);

        final PlayerJumpEvent jumpEvent = mock(PlayerJumpEvent.class);
        final String eventName = "PlayerJumpEvent";
        when(jumpEvent.getEventName()).thenReturn(eventName);
        assertThrows(QuestException.class, () -> group.callEvent(jumpEvent, EventPriority.NORMAL), eventName + "/" + EventPriority.NORMAL.name());
        assertThrows(QuestException.class, () -> group.callEvent(jumpEvent, EventPriority.HIGH), eventName + "/" + EventPriority.HIGH.name());
        when(jumpEvent.isCancelled()).thenReturn(true);
        assertThrows(QuestException.class, () -> group.callEvent(jumpEvent, EventPriority.NORMAL), eventName + "/" + EventPriority.NORMAL.name());
        assertDoesNotThrow(() -> group.callEvent(jumpEvent, EventPriority.HIGH));
    }

    @Test
    public void testRequireAndDisabling() throws QuestException {
        final Map<EventPriority, ProxyListener<PlayerJumpEvent>> listeners = new EnumMap<>(EventPriority.class);
        generateListeners(listeners, group);

        assertDoesNotThrow(() -> group.bake(mock(Plugin.class)));
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
        final EnumSet<EventPriority> stillEnabled = EnumSet.complementOf(toDisable);
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
