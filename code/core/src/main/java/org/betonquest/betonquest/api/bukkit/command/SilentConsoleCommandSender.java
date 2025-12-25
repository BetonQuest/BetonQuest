package org.betonquest.betonquest.api.bukkit.command;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A wrapper for a {@link ConsoleCommandSender} that does not send any messages.
 */
public class SilentConsoleCommandSender extends SilentCommandSender implements ConsoleCommandSender {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The wrapped command sender.
     */
    private final ConsoleCommandSender sender;

    /**
     * Create a new silent console command sender.
     *
     * @param log    the logger that will be used for logging
     * @param sender the command sender to wrap
     */
    public SilentConsoleCommandSender(final BetonQuestLogger log, final ConsoleCommandSender sender) {
        super(log, sender);
        this.log = log;
        this.sender = sender;
    }

    @Override
    public boolean isConversing() {
        return sender.isConversing();
    }

    @Override
    public void acceptConversationInput(final String input) {
        sender.acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(final Conversation conversation) {
        return sender.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(final Conversation conversation) {
        sender.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(final Conversation conversation, final ConversationAbandonedEvent details) {
        sender.abandonConversation(conversation, details);
    }

    @Override
    public void sendRawMessage(final String message) {
        log.debug("Silently sending message to console: " + message);
    }

    @Override
    public void sendRawMessage(@Nullable final UUID sender, final String message) {
        log.debug("Silently sending message to console: " + message);
    }
}
