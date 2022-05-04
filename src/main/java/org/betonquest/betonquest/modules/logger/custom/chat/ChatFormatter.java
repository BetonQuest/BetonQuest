package org.betonquest.betonquest.modules.logger.custom.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.betonquest.betonquest.modules.logger.custom.debug.LogfileFormatter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public class ChatFormatter extends LogfileFormatter {
    /**
     * The name of the plugin that is the source of this formatter.
     */
    private final String pluginName;
    /**
     * The method how to display the plugin name.
     */
    private final PluginDisplayMethod displayMethod;
    /**
     * The short name or tag of this plugin or null.
     */
    private final String shortName;

    /**
     * Create an ingame formatter for a given {@link Plugin}.
     */
    public ChatFormatter() {
        this(PluginDisplayMethod.NONE, null, null);
    }

    /**
     * Create an ingame formatter for a given {@link Plugin}.
     * If another displayMethod than @{@link PluginDisplayMethod#NONE} is chosen,
     * than it is not allowed to set plugin to null.
     *
     * @param displayMethod the method how to display the plugin name
     * @param plugin        the base {@link Plugin} for this formatter
     * @param shortName     the short name of the plugin or null
     */
    public ChatFormatter(@NotNull final PluginDisplayMethod displayMethod, @Nullable final Plugin plugin, @Nullable final String shortName) {
        super();
        if (displayMethod != PluginDisplayMethod.NONE && plugin == null) {
            throw new IllegalArgumentException("Plugin must be non null if displayMethod is not NONE");
        }
        this.pluginName = plugin == null ? null : plugin.getName();
        this.displayMethod = displayMethod;
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
        final boolean match = pluginName != null && pluginName.equals(plugin);
        final Pair<String, String> tagParts = getPluginTagParts(plugin, match);
        if (tagParts == null) {
            return "";
        }
        final String tag = tagParts.getLeft() + (tagParts.getLeft().isEmpty() || tagParts.getRight().isEmpty() ? "" : " | ") + tagParts.getRight();
        return ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + tag + ChatColor.GRAY + "]" + ChatColor.RESET + " ";
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.SwitchStmtsShouldHaveDefault"})
    private Pair<String, String> getPluginTagParts(final String plugin, final boolean match) {
        final String left;
        final String right;
        switch (displayMethod) {
            case NONE -> {
                return null;
            }
            case PLUGIN -> {
                left = match && shortName != null ? shortName : plugin;
                right = "";
            }
            case ROOT_PLUGIN -> {
                left = shortName == null ? pluginName : shortName;
                right = "";
            }
            case ROOT_PLUGIN_AND_PLUGIN -> {
                left = shortName == null ? pluginName : shortName;
                right = match ? "" : plugin;
            }
            default -> {
                left = "";
                right = "";
            }
        }
        return Pair.of(left, right);
    }

    /**
     * Display methods for the plugin name before the log message.
     */
    public enum PluginDisplayMethod {
        /**
         * Non plugin name before log messages.
         */
        NONE,
        /**
         * The plugin that created the log message.
         */
        PLUGIN,
        /**
         * Only the root plugin that created this formatter.
         */
        ROOT_PLUGIN,
        /**
         * The root plugin that created this formatter and the plugin that created the log message.
         */
        ROOT_PLUGIN_AND_PLUGIN,
    }
}
