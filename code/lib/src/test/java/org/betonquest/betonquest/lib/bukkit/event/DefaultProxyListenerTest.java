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

    /**
     * Create an empty listener.
     *
     * @return an empty listener
     */
    private static Listener empty() {
        return new Listener() {
        };
    }

    /**
     * Create an event executor that calls the given consumer.
     *
     * @param consumer the consumer to call with an event
     * @return the event executor
     */
    private static EventExecutor executor(final Consumer<Event> consumer) {
        return (listener, event) -> consumer.accept(event);
    }

    /**
     * Create a registered listener with an empty listener and an empty event executor.
     *
     * @param plugin the plugin to register the listener with
     * @return the registered listener
     */
    private static RegisteredListener empty(final Plugin plugin) {
        return new RegisteredListener(empty(), executor(e -> {
        }), EventPriority.NORMAL, plugin, false);
    }

    /**
     * Test creating a proxy listener with a poor event.
     *
     * @param plugin a mocked plugin instance
     */
    @Test
    void testCreateProxyListenerWithPoorEvent(@Mock final Plugin plugin) {
        assertThrows(QuestException.class, () -> new DefaultProxyListener<>(Event.class, empty(plugin)),
                "Creating a proxy listener with a poor event should fail");
    }

    /**
     * Test creating a proxy listener with a handler list extraction.
     *
     * @param plugin a mocked plugin instance
     */
    @Test
    void testCreateProxyListenerWithHandlerListExtraction(@Mock final Plugin plugin) throws QuestException {
        final DefaultProxyListener<PlayerJumpEvent> event = new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin));
        assertNotNull(event, "Event should not be null");
        assertEquals(PlayerJumpEvent.class, event.getEventType(), "Event type should be PlayerJumpEvent");
    }

    /**
     * Test creating a proxy listener with a handler list extraction.
     *
     * @param plugin a mocked plugin instance
     */
    @Test
    void testCreateProxyListenerWithHandlerListExtraction2(@Mock final Plugin plugin) throws QuestException {
        final DefaultProxyListener<PlayerJumpEvent> event = new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin));
        assertNotNull(event.handlerList, "Event's HandlerList should not be null");
        assertEquals(PlayerJumpEvent.getHandlerList(), event.handlerList, "Event's HandlerList should be the same as PlayerJumpEvent's");
    }

    /**
     * Test registering a proxy listener.
     *
     * @param plugin a mocked plugin instance
     */
    @Test
    void testProxyListenerHandlerListRegistration(@Mock final Plugin plugin) throws QuestException {
        final DefaultProxyListener<PlayerJumpEvent> event = new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin));
        assertFalse(event.isRegistered(), "Event should not be registered yet");
        event.register();
        assertTrue(event.isRegistered(), "Event should be registered");
    }

    /**
     * Test deregistering a proxy listener.
     *
     * @param plugin a mocked plugin instance
     */
    @Test
    void testProxyListenerHandlerListDeregistration(@Mock final Plugin plugin) throws QuestException {
        final DefaultProxyListener<PlayerJumpEvent> event = new DefaultProxyListener<>(PlayerJumpEvent.class, empty(plugin));
        event.register();
        assertTrue(event.isRegistered(), "Event should be registered");
        event.unregister();
        assertFalse(event.isRegistered(), "Event should not be registered anymore");
    }
}
