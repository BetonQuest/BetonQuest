package org.betonquest.betonquest.modules.logger.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
//import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a simple log formatting class for the in-game chat.
 */
public final class ChatFormatter extends Formatter {
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
        final TextColor color = formatColor(record.getLevel());
        final Optional<BetonQuestLogRecord> betonRecord = BetonQuestLogRecord.safeCast(record);
        final String plugin = betonRecord
                .map(BetonQuestLogRecord::getPlugin)
                .orElse("?");
        final String questPackage = betonRecord
                .flatMap(BetonQuestLogRecord::getPack)
                .map(pack -> "<" + pack + "> ")
                .orElse("");

        final String message = record.getMessage();
        final Component throwable = formatComponentThrowable(record);

        final TextComponent formattedRecord = displayMethod.getPluginTag(pluginName, plugin, shortName)
                .append(Component.text(questPackage))
                .append(Component.text(message, color))
                .append(throwable);
        return GsonComponentSerializer.gson().serialize(formattedRecord);
    }

    private TextColor formatColor(final Level level) {
        final int levelValue = level.intValue();
        if (levelValue >= Level.SEVERE.intValue()) {
            return NamedTextColor.DARK_RED;
        }
        if (levelValue >= Level.WARNING.intValue()) {
            return NamedTextColor.RED;
        }
        if (levelValue >= Level.INFO.intValue()) {
            return NamedTextColor.WHITE;
        }
        return NamedTextColor.GRAY;
    }

    /**
     * Formats a {@link LogRecord} to a readable chat {@link Component}.
     *
     * @param record The record to format
     * @return The formatted component
     */
    private Component formatComponentThrowable(final LogRecord record) {
        if (record.getThrown() == null) {
            return Component.empty();
        }
        final String throwable = FormatterUtils.formatThrowable(record.getThrown());
        return Component.text(" Hover for Stacktrace!", NamedTextColor.RED)
                .hoverEvent(Component.text(throwable.replace("\t", "  ").replace("\r", ""))
                        .append(Component.newline()).append(Component.newline())
                        .append(Component.text("Click to copy!", NamedTextColor.DARK_GREEN)))
                .clickEvent(ClickEvent.copyToClipboard(throwable));
    }

    /**
     * Display methods for the plugin name before the log message.
     */
    public enum PluginDisplayMethod {
        /**
         * Non plugin name before log messages.
         */
        NONE((params) -> null),
        /**
         * The plugin that created the log message.
         */
        PLUGIN((params) -> {
            final String plugin = params.match ? getPluginNameOrShortName(params) : params.otherPluginName();
            return Pair.of(plugin, "");
        }),
        /**
         * Only the root plugin that created this formatter.
         */
        ROOT_PLUGIN((params) -> Pair.of(getPluginNameOrShortName(params), "")),
        /**
         * The root plugin that created this formatter and the plugin that created the log message.
         */
        ROOT_PLUGIN_AND_PLUGIN((params) -> Pair.of(getPluginNameOrShortName(params), params.match ? "" : params.otherPluginName));

        /**
         * A function, that create the two tag parts.
         */
        private final Function<Parameters, Pair<String, String>> producer;

        PluginDisplayMethod(final Function<Parameters, Pair<String, String>> producer) {
            this.producer = producer;
        }

        private static String getPluginNameOrShortName(final Parameters params) {
            return params.shortName == null ? params.pluginName : params.shortName;
        }

        /**
         * Get the tag to display, related to the {@link PluginDisplayMethod}.
         *
         * @param pluginName      The plugin name of this plugin
         * @param otherPluginName The plugin name of the other plugin
         * @param shortName       The short tag of the pluginName
         * @return the processed plugin tag
         */
        public TextComponent getPluginTag(final String pluginName, final String otherPluginName, final String shortName) {
            final boolean match = pluginName != null && pluginName.equals(otherPluginName);
            final Pair<String, String> tagParts = producer.apply(new Parameters(pluginName, otherPluginName, shortName, match));
            if (tagParts == null) {
                return Component.empty();
            }
            final String tag = tagParts.getLeft() + (tagParts.getLeft().isEmpty() || tagParts.getRight().isEmpty() ? "" : " | ") + tagParts.getRight();
            return Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text(tag, NamedTextColor.DARK_GRAY))
                    .append(Component.text("]", NamedTextColor.GRAY))
                    .append(Component.text(" "));
        }

        /**
         * All data that should be passed to the producer function.
         *
         * @param pluginName      The name of the own plugin
         * @param otherPluginName The name of the actual plugin that logged the message
         * @param shortName       A short tag for the own plugin
         * @param match           true when pluginName and otherPluginName do match
         */
        private record Parameters(String pluginName, String otherPluginName, String shortName, boolean match) {
        }
    }
}
