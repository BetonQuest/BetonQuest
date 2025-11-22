package org.betonquest.betonquest.api.bukkit.command;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * This class tests the {@link SilentConsoleCommandSender} class.
 */
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

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void sendRawMessage() {
        silentSender.sendRawMessage("test1");
        verify(sender, never()).sendRawMessage("test1");
        verify(logger, times(1)).debug("Silently sending message to console: test1");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void sendRawMessage_sender() {
        silentSender.sendRawMessage(null, "test3");
        verify(sender, never()).sendRawMessage(any(UUID.class), anyString());
        verify(logger, times(1)).debug("Silently sending message to console: test3");
        verifyNoMoreInteractions(logger);
    }
}
