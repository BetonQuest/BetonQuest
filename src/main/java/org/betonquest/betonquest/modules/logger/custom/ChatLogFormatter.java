package org.betonquest.betonquest.modules.logger.custom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.bukkit.ChatColor;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public class ChatLogFormatter extends DebugLogFormatter {

    /**
     * Default constructor.
     */
    public ChatLogFormatter() {
        super();
    }

    @Override
    public String format(final LogRecord record) {
        if (!(record instanceof BetonQuestLogRecord logRecord)) {
            throw new UnsupportedOperationException("The ChatLogFormatter can only format BetonQuestLogRecords!");
        }
        final String color = formatColor(record.getLevel());
        final String plugin = logRecord.getPlugin();
        final String questPackage = logRecord.getPack().isEmpty() ? "" : "<" + logRecord.getPack() + "> ";
        final String message = record.getMessage();
        final Component throwable = formatComponentThrowable(record);

        final TextComponent formattedRecord = Component.text(getPluginTag(plugin) + questPackage + color + message)
                .append(throwable);
        return GsonComponentSerializer.gson().serialize(formattedRecord);
    }

    private String formatColor(final Level level) {
        final int levelValue = level.intValue();
        if (levelValue >= Level.SEVERE.intValue()) {
            return ChatColor.DARK_RED.toString();
        }
        if (levelValue >= Level.WARNING.intValue()) {
            return ChatColor.RED.toString();
        }
        if (levelValue >= Level.INFO.intValue()) {
            return ChatColor.WHITE.toString();
        }
        return ChatColor.GRAY.toString();
    }

    /**
     * Formats a {@link LogRecord} to a readable chat {@link Component}.
     *
     * @param record The record to format
     * @return The formatted component
     */
    protected Component formatComponentThrowable(final LogRecord record) {
        if (record.getThrown() == null) {
            return Component.empty();
        }
        final String throwable = formatThrowable(record);
        return Component.text(" Hover for Stacktrace!", NamedTextColor.RED)
                .hoverEvent(Component.text(throwable.replace("\t", "  ").replace("\r", ""))
                        .append(Component.newline()).append(Component.newline())
                        .append(Component.text("Click to copy!", NamedTextColor.DARK_GREEN)))
                .clickEvent(ClickEvent.copyToClipboard(throwable));
    }

    private String getPluginTag(final String plugin) {
        final String tag = "BetonQuest".equals(plugin) ? "BQ" : "BQ | " + plugin;
        return ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + tag + ChatColor.GRAY + "]" + ChatColor.RESET + " ";
    }
}
