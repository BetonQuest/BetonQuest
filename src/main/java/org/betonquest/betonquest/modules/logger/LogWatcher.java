package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.modules.logger.custom.chat.ChatHandler;
import org.betonquest.betonquest.modules.logger.custom.chat.PlayerFilter;
import org.betonquest.betonquest.modules.logger.custom.debug.HistoryHandler;
import org.betonquest.betonquest.modules.logger.custom.debug.LogPublishingController;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Setups the log for the plugin.
 */
public final class LogWatcher implements AutoCloseable {
    /**
     * The parent logger the {@link java.util.logging.Handler}s where added to.
     */
    private final Logger parentLogger;
    /**
     * The {@link HistoryHandler} that holds old LogRecords.
     */
    private final HistoryHandler historyHandler;
    /**
     * The {@link ChatHandler} that holds old LogRecords.
     */
    private final ChatHandler chatHandler;

    /**
     * Setups the debug and in-game chat log.
     *
     * @param plugin         The {@link Plugin} to link this setup to
     * @param historyHandler {@link HistoryHandler} instance
     * @param chatHandler    {@link ChatHandler} instance
     */
    public LogWatcher(final Plugin plugin, final HistoryHandler historyHandler, final ChatHandler chatHandler) {
        this.parentLogger = plugin.getServer().getLogger().getParent();
        this.historyHandler = historyHandler;
        this.chatHandler = chatHandler;

        parentLogger.addHandler(historyHandler);
        parentLogger.addHandler(chatHandler);
    }

    /**
     * Get the {@link PlayerFilter} instance for the {@link ChatHandler}.
     *
     * @return the instance of the {@link PlayerFilter}
     */
    public PlayerFilter getPlayerFilter() {
        return chatHandler.getPlayerFilter();
    }

    /**
     * Get the {@link LogPublishingController} instance for the {@link HistoryHandler}.
     *
     * @return the instance of the {@link LogPublishingController}
     */
    public LogPublishingController getDebuggingController() {
        return historyHandler;
    }

    @Override
    public void close() {
        parentLogger.removeHandler(chatHandler);
        parentLogger.removeHandler(historyHandler);
        chatHandler.close();
        historyHandler.close();
    }
}
