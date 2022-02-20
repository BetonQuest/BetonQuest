package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.bukkit.plugin.Plugin;

import java.io.Serial;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Custom {@link LogRecord} for BetonQuest that adds a {@link QuestPackage} name.
 */
public class BetonQuestLogRecord extends LogRecord {

    @Serial
    private static final long serialVersionUID = -7094531905051980356L;

    /**
     * The plugin that logged this message.
     */
    private final String plugin;

    /**
     * The name of the originating {@link QuestPackage}.
     */
    private final String pack;

    /**
     * Creates a custom {@link LogRecord} that contains a {@link QuestPackage} and {@link Plugin} name.
     *
     * @param plugin The {@link Plugin}, that logged this message.
     * @param pack   The {@link QuestPackage} that belongs to this LogRecord.
     * @param level  A log {@link Level}.
     * @param msg    The raw non-localized logging message (may be null).
     */
    public BetonQuestLogRecord(final Plugin plugin, final QuestPackage pack, final Level level, final String msg) {
        super(level, msg);
        this.plugin = plugin == null ? "" : plugin.getName();
        this.pack = pack == null ? "" : pack.getPackagePath();
    }

    /**
     * Gets the {@link QuestPackage} name.
     *
     * @return Returns the package name.
     */
    public String getPack() {
        return pack;
    }

    /**
     * Gets the {@link Plugin}, that logged this message.
     *
     * @return The plugin.
     */
    public String getPlugin() {
        return plugin;
    }
}
