package org.betonquest.betonquest.logger.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Require any logged {@link LogRecord} to be of a certain type.
 * This filter will also allow subclasses of the filtered type.
 */
public class LogRecordTypeFilter implements Filter {
    /**
     * The required type.
     */
    private final Class<? extends LogRecord> requiredRecordType;

    /**
     * Create the {@link Filter} for the given {@link LogRecord} type.
     *
     * @param requiredRecordType the required type
     */
    public LogRecordTypeFilter(final Class<? extends LogRecord> requiredRecordType) {
        this.requiredRecordType = requiredRecordType;
    }

    @Override
    public boolean isLoggable(final LogRecord record) {
        return requiredRecordType.isInstance(record);
    }
}
