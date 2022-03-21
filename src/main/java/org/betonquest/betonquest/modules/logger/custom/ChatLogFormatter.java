package org.betonquest.betonquest.modules.logger.custom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public class ChatLogFormatter extends DebugLogFormatter {
    /**
     * The name of the plugin that is the source of this formatter.
     */
    private final String pluginName;
    /**
     * The short name or tag of this plugin or null.
     */
    private final String shortName;

    /**
     * Create an ingame formatter for a given {@link Plugin}.
     *
     * @param plugin    the base {@link Plugin} for this formatter
     * @param shortName the short name of the plugin or null
     */
    public ChatLogFormatter(@NotNull final Plugin plugin, @Nullable final String shortName) {
        super();
        this.pluginName = plugin.getName();
        this.shortName = shortName;
    }

    @Override
    public String format(final LogRecord record) {
        final String color = formatColor(record.getLevel());
        final BetonQuestLogRecord logRecord = record instanceof BetonQuestLogRecord ? (BetonQuestLogRecord) record : null;
        final String plugin = logRecord == null ? "?" : logRecord.getPlugin();
        final String questPackage = logRecord == null || logRecord.getPack().isEmpty() ? "" : "<" + logRecord.getPack() + "> ";

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
        final String pluginTag = shortName == null ? plugin : shortName;
        final String tag = pluginName.equals(plugin) ? pluginTag : pluginTag + " | " + plugin;
        return ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + tag + ChatColor.GRAY + "]" + ChatColor.RESET + " ";
    }
}
