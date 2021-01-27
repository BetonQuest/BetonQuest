package org.betonquest.betonquest.utils.logger;

import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Custom {@link LogRecord} for BetonQuest to add a additional package name.
 */
class BetonQuestLogRecord extends LogRecord {

    private static final long serialVersionUID = -7094531905051980356L;
    /**
     * The quest packages name.
     * -- GETTER --
     * Gets the quest packages name.
     *
     * @return Returns the quest package name.
     */
    @Getter
    private final String pack;

    /**
     * Construct a LogRecord with the given level and message values.
     * <p>
     * The sequence property will be initialized with a new unique value.
     * These sequence values are allocated in increasing order within a VM.
     * <p>
     * The millis property will be initialized to the current time.
     * <p>
     * The thread ID property will be initialized with a unique ID for
     * the current thread.
     * <p>
     * All other properties will be initialized to "null".
     *
     * @param configPackage The quest package this LogRecord came from.
     * @param level         A logging level value.
     * @param msg           The raw non-localized logging message (may be null).
     */
    public BetonQuestLogRecord(final String configPackage, final Level level, final String msg) {
        super(level, msg);
        this.pack = configPackage;
    }
}
