package org.betonquest.betonquest.logger.handler.chat;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.logger.format.ChatFormatter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.mockito.Mockito.*;

/**
 * Test {@link ChatHandler}.
 */
@ExtendWith(MockitoExtension.class)
class ChatHandlerTest {

    @Test
    void testLoggingToPlayersChat(
            @Mock final RecordReceiverSelector selector,
            @Mock final Server server,
            @Mock final Player player) {
        final UUID uuid = UUID.randomUUID();
        when(selector.findReceivers(any())).thenReturn(Set.of(uuid));
        when(server.getPlayer(uuid)).thenReturn(player);
        final String message = "test message";
        final LogRecord record = new LogRecord(Level.INFO, message);

        final ChatHandler handler = new ChatHandler(server, selector);
        handler.setFormatter(new ChatFormatter());

        handler.publish(record);

        verify(player).sendMessage(any(Component.class));
        handler.flush();
        handler.close();
    }

    @Test
    void testFormatException() {
        final RecordReceiverSelector recordReceiverSelector = mock(RecordReceiverSelector.class);
        when(recordReceiverSelector.findReceivers(any())).thenReturn(Set.of(UUID.randomUUID()));
        final ChatHandler handler = new ChatHandler(mock(Server.class), recordReceiverSelector);
        final Formatter formatter = mock(Formatter.class);
        when(formatter.format(any())).thenThrow(new RuntimeException());
        handler.setFormatter(formatter);
        final ErrorManager errorManager = mock(ErrorManager.class);
        handler.setErrorManager(errorManager);

        handler.publish(new LogRecord(Level.INFO, ""));
        verify(errorManager).error(any(), any(RuntimeException.class), anyInt());
    }
}
