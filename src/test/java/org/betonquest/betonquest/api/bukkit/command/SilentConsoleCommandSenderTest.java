package org.betonquest.betonquest.api.bukkit.command;

import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;
import java.util.logging.Level;

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
    public SilentConsoleCommandSender getSilentCommandSender() {
        silentSender = new SilentConsoleCommandSender(sender);
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
    void testAbandonConversation() {
        silentSender.abandonConversation(mock(Conversation.class), mock(ConversationAbandonedEvent.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class), any(ConversationAbandonedEvent.class));
    }

    @Test
    void sendRawMessage(final LogValidator validator) {
        silentSender.sendRawMessage("test1");
        verify(sender, never()).sendRawMessage("test1");
        validator.assertLogEntry(Level.FINE, "(SilentConsoleCommandSender) Silently sending message to console: test1");
        validator.assertEmpty();
    }

    @Test
    void testSendRawMessage(final LogValidator validator) {
        silentSender.sendRawMessage(null, "test3");
        verify(sender, never()).sendRawMessage(any(UUID.class), anyString());
        validator.assertLogEntry(Level.FINE, "(SilentConsoleCommandSender) Silently sending message to console: test3");
        validator.assertEmpty();
    }
}
