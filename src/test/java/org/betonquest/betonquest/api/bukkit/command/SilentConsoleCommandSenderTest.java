package org.betonquest.betonquest.api.bukkit.command;

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
     * The sender to use.
     */
    private final ConsoleCommandSender sender;

    /**
     * Create a new SilentConsoleCommandSenderTest instance.
     */
    public SilentConsoleCommandSenderTest() {
        this(mock(ConsoleCommandSender.class));
    }

    /**
     * Create a new SilentConsoleCommandSenderTest instance.
     *
     * @param sender the sender to use
     */
    public SilentConsoleCommandSenderTest(final ConsoleCommandSender sender) {
        super(sender);
        this.sender = sender;
    }

    @Override
    public SilentConsoleCommandSender getSilentCommandSender() {
        return new SilentConsoleCommandSender(sender);
    }

    @Test
    void isConversing() {
        getSilentCommandSender().isConversing();
        verify(sender, times(1)).isConversing();
    }

    @Test
    void acceptConversationInput() {
        getSilentCommandSender().acceptConversationInput("test");
        verify(sender, times(1)).acceptConversationInput("test");
    }

    @Test
    void beginConversation() {
        getSilentCommandSender().beginConversation(mock(Conversation.class));
        verify(sender, times(1)).beginConversation(any(Conversation.class));
    }

    @Test
    void abandonConversation() {
        getSilentCommandSender().abandonConversation(mock(Conversation.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class));
    }

    @Test
    void testAbandonConversation() {
        getSilentCommandSender().abandonConversation(mock(Conversation.class), mock(ConversationAbandonedEvent.class));
        verify(sender, times(1)).abandonConversation(any(Conversation.class), any(ConversationAbandonedEvent.class));
    }

    @Test
    void sendRawMessage() {
        getSilentCommandSender().sendRawMessage("test1");
        verify(sender, times(1)).sendRawMessage("test1");
    }

    @Test
    void testSendRawMessage() {
        getSilentCommandSender().sendRawMessage(null, "test3");
        verify(sender, times(1)).sendRawMessage(any(UUID.class), anyString());
    }
}
