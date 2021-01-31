package org.betonquest.betonquest.utils.logger.custom;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class PlayerHandler extends Handler {

    public PlayerHandler() {
        super();
    }

    /**
     * Log a LogRecord to the history or the target Handler.
     *
     * @param record The LogRecord to log.
     */
    @Override
    public void publish(final LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        final String msg;
        try {
            msg = getFormatter().format(record);
        } catch (final Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
