package org.betonquest.betonquest.api.bukkit.command;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * This class tests the {@link SilentConsoleCommandSender} class.
 */
@ExtendWith(BetonQuestLoggerService.class)
class SilentConsoleCommandSenderTest extends SilentCommandSenderTest {
    /**
     * The console command sender to use.
     */
    private ConsoleCommandSender sender;

    /**
     * The silent console command sender to use.
     */
    private SilentConsoleCommandSender silentSender;

    @Override
    public ConsoleCommandSender getCommandSender() {
        sender = mock(ConsoleCommandSender.class);
        return sender;
    }

    @Override
    public SilentConsoleCommandSender getSilentCommandSender(final BetonQuestLogger logger) {
        silentSender = new SilentConsoleCommandSender(logger, sender);
        return silentSender;
    }

    @Test
    void isConversing() {
        silentSender.isConversing();
        verify(sender, times(1)).isConversing();
    }

    @Test
    void acceptConversationInput() {
        silentSender.acceptConversationInput("test");
        verify(sender, times(1)).acceptConversationInput("test");
    }

    @Test
    void beginConversation() {
        silentSender.beginConversation(mock(Conversation.class));
        verify(sender, times(1)).beginConversation(any(Conversation.class));
    }

    @Test
    void abandonConversation() {
        silentSender.abandonConversation(mock(Conversation.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class));
    }

    @Test
    void abandonConversation_ConversationAbandonedEvent() {
        silentSender.abandonConversation(mock(Conversation.class), mock(ConversationAbandonedEvent.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class), any(ConversationAbandonedEvent.class));
    }

    @Test
    void sendRawMessage(final BetonQuestLogger logger) {
        silentSender.sendRawMessage("test1");
        verify(sender, never()).sendRawMessage("test1");
        verify(logger, times(1)).debug("Silently sending message to console: test1");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void sendRawMessage_sender(final BetonQuestLogger logger) {
        silentSender.sendRawMessage(null, "test3");
        verify(sender, never()).sendRawMessage(any(UUID.class), anyString());
        verify(logger, times(1)).debug("Silently sending message to console: test3");
        verifyNoMoreInteractions(logger);
    }
}
