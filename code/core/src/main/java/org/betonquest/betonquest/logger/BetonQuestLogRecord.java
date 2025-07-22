package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Custom {@link LogRecord} for BetonQuest that adds a {@link Plugin} name and {@link QuestPackage} name.
 */
public class BetonQuestLogRecord extends LogRecord {
    @Serial
    private static final long serialVersionUID = -7094531905051980356L;

    /**
     * The plugin that logged this message.
     */
    private final String plugin;

    /**
     * The fully qualified name of the quest package that this log record is
     * about or null if the record is not specific to a quest package.
     */
    @Nullable
    private final String pack;

    /**
     * Creates a custom {@link LogRecord} that comes from a {@link Plugin} and is not specific to any {@link QuestPackage}.
     *
     * @param level   level of the LogRecord
     * @param message raw non-localized logging message (may be null)
     * @param plugin  plugin that logged this LogRecord
     */
    public BetonQuestLogRecord(final Level level, @Nullable final String message, final Plugin plugin) {
        this(level, message, plugin, null);
    }

    /**
     * Creates a custom {@link LogRecord} that comes from a {@link Plugin} and may be specific to a {@link QuestPackage}.
     *
     * @param level   level of the LogRecord
     * @param message raw non-localized logging message (may be null)
     * @param plugin  plugin that logged this LogRecord
     * @param pack    quest package this LogRecord is about; or null if it is not about any specific package
     */
    public BetonQuestLogRecord(final Level level, @Nullable final String message, final Plugin plugin, @Nullable final QuestPackage pack) {
        this(level, message, plugin.getName(), pack == null ? null : pack.getQuestPath());
    }

    /**
     * Creates a custom {@link LogRecord} that comes from a plugin and may be specific to a quest package.
     *
     * @param level       level of the LogRecord
     * @param message     raw non-localized logging message (may be null)
     * @param pluginName  name of the plugin that logged this LogRecord
     * @param packageName fully qualified name of the quest package this LogRecord is about;
     *                    or null if it is not about any specific package
     */
    public BetonQuestLogRecord(final Level level, @Nullable final String message, final String pluginName, @Nullable final String packageName) {
        super(level, message);
        this.plugin = pluginName;
        this.pack = packageName;
    }

    /**
     * Try to cast a {@link LogRecord} into a {@link BetonQuestLogRecord} and return an {@link Optional} that contains
     * the cast record if successful.
     *
     * @param record record that may be a {@link BetonQuestLogRecord}
     * @return optional containing the same record as {@link BetonQuestLogRecord} if possible
     */
    public static Optional<BetonQuestLogRecord> safeCast(final LogRecord record) {
        if (record instanceof BetonQuestLogRecord) {
            return Optional.of((BetonQuestLogRecord) record);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets the fully qualified name of the quest package if present that this record is about.
     *
     * @return optional containing the fully qualified package name
     */
    public Optional<String> getPack() {
        return Optional.ofNullable(pack);
    }

    /**
     * Gets the name of the plugin that logged this record.
     *
     * @return The plugin.
     */
    public String getPlugin() {
        return plugin;
    }
}
