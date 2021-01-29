package org.betonquest.betonquest.utils.logger.custom;

import lombok.Getter;
import org.betonquest.betonquest.config.ConfigPackage;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Custom {@link LogRecord} for BetonQuest that adds a package name.
 */
public class PackageLogRecord extends LogRecord {

    private static final long serialVersionUID = -7094531905051980356L;
    /**
     * The package name.
     * -- GETTER --
     * Gets the package name.
     *
     * @return Returns the package name.
     */
    @Getter
    private final String pack;

    /**
     * Sets the package and calls the original method {@link LogRecord#LogRecord(Level, String)}.
     *
     * @param pack  The package this LogRecord came from.
     * @param level A logging level value.
     * @param msg   The raw non-localized logging message (may be null).
     */
    public PackageLogRecord(final ConfigPackage pack, final Level level, final String msg) {
        super(level, msg);
        this.pack = pack == null ? "" : pack.getName();
    }
}
