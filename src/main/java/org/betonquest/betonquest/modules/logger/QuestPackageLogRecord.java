package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.config.ConfigPackage;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Custom {@link LogRecord} for BetonQuest that adds a {@link ConfigPackage} name.
 */
public class QuestPackageLogRecord extends LogRecord {

    private static final long serialVersionUID = -7094531905051980356L;
    /**
     * The plugin where the log message comes from.
     */
    private final String plugin;
    /**
     * The package name.
     */
    private final String pack;

    /**
     * Sets the package and calls the original method {@link LogRecord#LogRecord(Level, String)}.
     *
     * @param plugin The {@link Plugin}, where this log message comes from.
     * @param pack   The {@link ConfigPackage} this LogRecord came from.
     * @param level  A logging level value.
     * @param msg    The raw non-localized logging message (may be null).
     */
    public QuestPackageLogRecord(final Plugin plugin, final ConfigPackage pack, final Level level, final String msg) {
        super(level, msg);
        this.plugin = plugin == null ? "" : plugin.getName();
        this.pack = pack == null ? "" : pack.getName();
    }

    /**
     * Gets the package name.
     *
     * @return Returns the package name.
     */
    public String getPack() {
        return pack;
    }

    /**
     * Get the plugin, where this log message comes from.
     *
     * @return The plugin.
     */
    public String getPlugin() {
        return plugin;
    }
}
