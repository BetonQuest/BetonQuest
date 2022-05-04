package org.betonquest.betonquest.modules.logger.custom.chat;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;

import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public class ChatHandler extends Handler {

    /**
     * The {@link PlayerFilter} for this {@link Handler}.
     */
    private final PlayerFilter playerFilter;

    /**
     * The {@link BukkitAudiences} instance responsible for sending messages.
     */
    private final BukkitAudiences bukkitAudiences;

    /**
     * Creates a new {@link ChatHandler}.
     *
     * @param playerFilter    A filter instance for this handler.
     * @param bukkitAudiences The {@link BukkitAudiences} instance for sending messages.
     */
    public ChatHandler(final PlayerFilter playerFilter, final BukkitAudiences bukkitAudiences) {
        super();
        this.playerFilter = playerFilter;
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
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void publish(final LogRecord record) {
        if (!(record instanceof BetonQuestLogRecord) || playerFilter.getUUIDs().isEmpty() || !isLoggable(record)) {
            return;
        }
        final String msg;
        try {
            msg = getFormatter().format(record);
        } catch (final Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        final String pack = ((BetonQuestLogRecord) record).getPack();
        filterPlayers(record, msg, pack);
    }

    private void filterPlayers(final LogRecord record, final String msg, final String pack) {
        for (final UUID uuid : playerFilter.getUUIDs()) {
            if (playerFilter.filter(uuid, pack, record.getLevel())) {
                bukkitAudiences.player(uuid).sendMessage(GsonComponentSerializer.gson().deserialize(msg));
            }
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

    /**
     * Get the {@link PlayerFilter} related to this {@link ChatHandler}
     *
     * @return a {@link PlayerFilter} instance
     */
    public PlayerFilter getPlayerFilter() {
        return playerFilter;
    }
}
