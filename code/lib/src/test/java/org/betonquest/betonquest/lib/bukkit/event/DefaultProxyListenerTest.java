package org.betonquest.betonquest.lib.bukkit.event;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultProxyListenerTest {

    @Mock
    private static Plugin plugin;

    private static Listener empty() {
        return new Listener() {
        };
    }

    private static EventExecutor executor(final Consumer<Event> consumer) {
        return (listener, event) -> consumer.accept(event);
    }

    private static RegisteredListener empty(final Plugin plugin) {
        return new RegisteredListener(empty(), executor(e -> {
        }), EventPriority.NORMAL, plugin, false);
    }

    @Test
    void poor_event_test() {
        assertThrows(QuestException.class, () -> new DefaultProxyListener<>(Event.class, empty(plugin)),
                "Creating a proxy listener with a poor event should fail");
    }

    @Nested
    class Listeners {

        private static MockedStatic<PlayerJumpEvent> mockedStatic;

        private DefaultProxyListener<PlayerJumpEvent> proxyListener;

        @BeforeAll
        static void setupBeforeAll() {
            mockedStatic = mockStatic(PlayerJumpEvent.class);
        }

        @AfterAll
        static void teardown() {
            mockedStatic.close();
        }

        @BeforeEach
        void setup() throws QuestException {
            mockedStatic.when(PlayerJumpEvent::getHandlerList).thenReturn(spy(new HandlerList()));
            proxyListener = spy(new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin)));
        }

        @Test
        void event_class_extraction() throws QuestException {
            assertNotNull(proxyListener, "Event should not be null");
            assertEquals(PlayerJumpEvent.class, proxyListener.getEventType(), "Event type should be PlayerJumpEvent");
        }

        @Test
        void handler_list_extraction() {
            assertNotNull(proxyListener.handlerList, "Event's HandlerList should not be null");
            assertEquals(PlayerJumpEvent.getHandlerList(), proxyListener.handlerList, "Event's HandlerList should be the same as PlayerJumpEvent's");
        }

        @Test
        void handler_list_register_calls_before() {
            verify(proxyListener.handlerList, never()).register(any());
            verify(proxyListener.handlerList, never()).bake();
            proxyListener.register();
        }

        @Test
        void handler_list_register_calls_after() {
            proxyListener.register();
            verify(proxyListener.handlerList, times(1)).register(any());
            verify(proxyListener.handlerList, times(1)).bake();
        }

        @Test
        void handler_list_unregister_calls_before() {
            proxyListener.register();
            verify(proxyListener.handlerList, never()).unregister((RegisteredListener) any());
            verify(proxyListener.handlerList, times(1)).bake();
            proxyListener.unregister();
        }

        @Test
        void handler_list_unregister_calls_after() {
            proxyListener.register();
            proxyListener.unregister();
            verify(proxyListener.handlerList, times(1)).unregister((RegisteredListener) any());
            verify(proxyListener.handlerList, times(2)).bake();
        }

        @Test
        void register() throws QuestException {
            assertFalse(proxyListener.isRegistered(), "Event should not be registered yet");
            proxyListener.register();
            assertTrue(proxyListener.isRegistered(), "Event should be registered");
        }

        @Test
        void unregister() throws QuestException {
            proxyListener.register();
            assertTrue(proxyListener.isRegistered(), "Event should be registered");
            proxyListener.unregister();
            assertFalse(proxyListener.isRegistered(), "Event should not be registered anymore");
        }
    }
}
