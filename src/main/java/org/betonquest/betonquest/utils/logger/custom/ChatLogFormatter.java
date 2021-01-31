package org.betonquest.betonquest.utils.logger.custom;

import org.betonquest.betonquest.BetonQuest;
import org.bukkit.ChatColor;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the ingame chat.
 */
public class ChatLogFormatter extends Formatter {

    /**
     * Default constructor.
     */
    public ChatLogFormatter() {
        super();
    }

    @Override
    public String format(final LogRecord record) {
        final String color = formatColor(record);
        final String message = record.getMessage();
        final String throwable = formatThrowable(record);

        return BetonQuest.getInstance().getPluginTag() + color + message + throwable;
    }

    private String formatColor(final LogRecord record) {
        final int level = record.getLevel().intValue();
        if (level >= Level.SEVERE.intValue()) {
            return ChatColor.DARK_RED.toString();
        }
        if (level >= Level.WARNING.intValue()) {
            return ChatColor.RED.toString();
        }
        if (level >= Level.INFO.intValue()) {
            return ChatColor.GRAY.toString();
        }
        return ChatColor.WHITE.toString();
    }

    private String formatThrowable(final LogRecord record) {
        final StringBuilder throwable = new StringBuilder();
        if (record.getThrown() != null) {
            throwable.append("\n").append("Full error in the log or debug log.");
            if (record.getThrown().getMessage() != null && record.getLevel() == Level.SEVERE) {
                throwable.append(" ").append(record.getThrown().getMessage());
            }
        }
        return throwable.toString();
    }
}
