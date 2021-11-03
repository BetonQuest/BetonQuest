package org.betonquest.betonquest.modules.logger.custom;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;

import java.util.Map;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public class PlayerLogHandler extends Handler {
    /**
     * The {@link BukkitAudiences} instance for sending messages.
     */
    private final BukkitAudiences bukkitAudiences;
    /**
     * All active log filters for the in-game log.
     */
    private final Map<UUID, Map<String, Level>> playerFilters;

    /**
     * Creates a new {@link PlayerLogHandler}.
     *
     * @param bukkitAudiences The {@link BukkitAudiences} instance for sending messages.
     * @param playerFilters   The map pointer with the filters
     */
    public PlayerLogHandler(final BukkitAudiences bukkitAudiences, final Map<UUID, Map<String, Level>> playerFilters) {
        super();
        this.bukkitAudiences = bukkitAudiences;
        this.playerFilters = playerFilters;
    }

    /**
     * Log a LogRecord to the history or the target handler.
     * Also compare to the code of the {@link java.util.logging.Logger}.publish
     *
     * @param record The LogRecord to log
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void publish(final LogRecord record) {
        if (!(record instanceof BetonQuestLogRecord) || !isLoggable(record)) {
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
        players:
        for (final Map.Entry<UUID, Map<String, Level>> filterEntries : playerFilters.entrySet()) {
            for (final Map.Entry<String, Level> entry : filterEntries.getValue().entrySet()) {
                if (record.getLevel().intValue() < entry.getValue().intValue()) {
                    continue;
                }
                final boolean equal = !entry.getKey().endsWith("*");
                final String expression = equal ? entry.getKey() : StringUtils.chop(entry.getKey());
                if (equal && pack.equals(expression) || !equal && pack.startsWith(expression)) {
                    bukkitAudiences.player(filterEntries.getKey()).sendMessage(GsonComponentSerializer.gson().deserialize(msg));
                    continue players;
                }
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
}
