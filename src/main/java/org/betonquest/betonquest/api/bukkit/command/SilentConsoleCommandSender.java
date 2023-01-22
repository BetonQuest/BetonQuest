package org.betonquest.betonquest.api.bukkit.command;

import lombok.CustomLog;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A wrapper for a {@link ConsoleCommandSender} that does not send any messages.
 */
@CustomLog(topic = "SilentConsoleCommandSender")
public class SilentConsoleCommandSender extends SilentCommandSender implements ConsoleCommandSender {
    /**
     * The wrapped command sender.
     */
    private final ConsoleCommandSender sender;

    /**
     * Create a new silent console command sender.
     *
     * @param sender the command sender to wrap
     */
    public SilentConsoleCommandSender(final ConsoleCommandSender sender) {
        super(sender);
        this.sender = sender;
    }

    @Override
    public boolean isConversing() {
        return sender.isConversing();
    }

    @Override
    public void acceptConversationInput(@NotNull final String input) {
        sender.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(@NotNull final Conversation conversation) {
        return sender.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull final Conversation conversation) {
        sender.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(@NotNull final Conversation conversation, @NotNull final ConversationAbandonedEvent details) {
        sender.abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(@NotNull final String message) {
        LOG.debug("Silently sending message to console: " + message);
    }

    @Override
    public void sendRawMessage(@Nullable final UUID sender, @NotNull final String message) {
        LOG.debug("Silently sending message to console: " + message);
    }
}
