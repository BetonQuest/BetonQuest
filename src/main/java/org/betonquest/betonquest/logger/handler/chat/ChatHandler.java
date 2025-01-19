package org.betonquest.betonquest.logger.handler.chat;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This {@link Handler} can send log messages via the ingame chat to a dynamic set of players.
 */
public class ChatHandler extends Handler {

    /**
     * Selector to decide the players that should receive a given {@link LogRecord}.
     */
    private final RecordReceiverSelector receiverSelector;

    /**
     * The {@link BukkitAudiences} instance responsible for sending messages.
     */
    private final BukkitAudiences bukkitAudiences;

    /**
     * Creates a new {@link ChatHandler}.
     *
     * @param receiverSelector a selector to decide the receiving players
     * @param bukkitAudiences  The {@link BukkitAudiences} instance for sending messages.
     */
    public ChatHandler(final RecordReceiverSelector receiverSelector, final BukkitAudiences bukkitAudiences) {
        super();
        this.receiverSelector = receiverSelector;
        this.bukkitAudiences = bukkitAudiences;
    }

    /**
     * Logs a LogRecord to the history or the target handler.
     * <br><br>
     * The {@link java.util.logging.MemoryHandler#publish} method was used as reference.
     *
     * @param record The LogRecord to log
     */
    @Override
    public void publish(final LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }

        final Set<UUID> receivers = receiverSelector.findReceivers(record);
        if (receivers.isEmpty()) {
            return;
        }

        final String message = format(record);
        if (message == null) {
            return;
        }

        sendMessageToPlayers(receivers, message);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Nullable
    private String format(final LogRecord record) {
        try {
            return getFormatter().format(record);
        } catch (final RuntimeException ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return null;
        }
    }

    private void sendMessageToPlayers(final Set<UUID> receivers, final String msg) {
        for (final UUID uuid : receivers) {
            bukkitAudiences.player(uuid).sendMessage(GsonComponentSerializer.gson().deserialize(msg));
        }
    }

    @Override
    public void flush() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
