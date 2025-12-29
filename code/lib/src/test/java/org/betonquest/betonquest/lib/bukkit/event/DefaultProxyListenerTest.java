package org.betonquest.betonquest.lib.bukkit.event;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.betonquest.betonquest.api.QuestException;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link DefaultProxyListener}.
 */
@ExtendWith(MockitoExtension.class)
class DefaultProxyListenerTest {

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
    public void testCreateProxyListenerWithPoorEvent(@Mock final Plugin plugin) {
        assertThrows(QuestException.class, () -> new DefaultProxyListener<>(Event.class, empty(plugin)));
    }

    @Test
    public void testCreateProxyListenerWithHandlerListExtraction(@Mock final Plugin plugin) {
        final DefaultProxyListener<PlayerJumpEvent> event = assertDoesNotThrow(() -> new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin)));
        assertNotNull(event);
        assertEquals(PlayerJumpEvent.class, event.getEventType());
        assertNotNull(event.handlerList);
        assertEquals(PlayerJumpEvent.getHandlerList(), event.handlerList);
    }

    @Test
    public void testProxyListenerHandlerListRegistration(@Mock final Plugin plugin) {
        final DefaultProxyListener<PlayerJumpEvent> event = assertDoesNotThrow(() -> new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin)));
        assertNotNull(event);
        assertFalse(event.isRegistered());
        event.register();
        assertTrue(event.isRegistered());
        event.register();
        assertTrue(event.isRegistered());
    }

    @Test
    public void testProxyListenerHandlerListDeregistration(@Mock final Plugin plugin) {
        final DefaultProxyListener<PlayerJumpEvent> event = assertDoesNotThrow(() -> new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin)));
        assertNotNull(event);
        assertFalse(event.isRegistered());
        event.register();
        assertTrue(event.isRegistered());
        event.unregister();
        assertFalse(event.isRegistered());
        event.unregister();
        assertFalse(event.isRegistered());
    }
}
