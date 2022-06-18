package org.betonquest.betonquest.modules.logger.handler.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.betonquest.betonquest.modules.logger.format.ChatFormatter;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.mockito.Mockito.*;

/**
 * Test {@link ChatHandler}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class ChatHandlerTest {
    /**
     * Default constructor.
     */
    public ChatHandlerTest() {
        // Empty
    }

    @Test
    void testLoggingToPlayersChat(
            @Mock final RecordReceiverSelector selector,
            @Mock final BukkitAudiences audiences,
            @Mock final Audience audience) {
        final UUID uuid = UUID.randomUUID();
        when(selector.findReceivers(any())).thenReturn(Set.of(uuid));
        when(audiences.player(uuid)).thenReturn(audience);
        final String message = "test message";
        final LogRecord record = new LogRecord(Level.INFO, message);

        final ChatHandler handler = new ChatHandler(selector, audiences);
        handler.setFormatter(new ChatFormatter());

        handler.publish(record);

        verify(audience).sendMessage(any());
    }
}
