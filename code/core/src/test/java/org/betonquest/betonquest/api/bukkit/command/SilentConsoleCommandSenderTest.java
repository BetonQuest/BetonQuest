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
    protected ConsoleCommandSender getCommandSender() {
        sender = mock(ConsoleCommandSender.class);
        return sender;
    }

    @Override
    protected SilentConsoleCommandSender getSilentCommandSender() {
        silentSender = new SilentConsoleCommandSender(logger, sender);
        return silentSender;
    }

    @Test
    void is_conversing() {
        silentSender.isConversing();
        verify(sender, times(1)).isConversing();
    }

    @Test
    void accept_conversation_input() {
        silentSender.acceptConversationInput("test");
        verify(sender, times(1)).acceptConversationInput("test");
    }

    @Test
    void begin_conversation() {
        silentSender.beginConversation(mock(Conversation.class));
        verify(sender, times(1)).beginConversation(any(Conversation.class));
    }

    @Test
    void abandon_conversation() {
        silentSender.abandonConversation(mock(Conversation.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class));
    }

    @Test
    void abandon_conversation_conversation_abandoned_event() {
        silentSender.abandonConversation(mock(Conversation.class), mock(ConversationAbandonedEvent.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class), any(ConversationAbandonedEvent.class));
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void send_raw_message() {
        silentSender.sendRawMessage("test1");
        verify(sender, never()).sendRawMessage("test1");
        verify(logger, times(1)).debug("Silently sending message to console 'test1'.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void send_raw_message_sender() {
        silentSender.sendRawMessage(null, "test3");
        verify(sender, never()).sendRawMessage(any(UUID.class), anyString());
        verify(logger, times(1)).debug("Silently sending message to console 'test3'.");
        verifyNoMoreInteractions(logger);
    }
}
