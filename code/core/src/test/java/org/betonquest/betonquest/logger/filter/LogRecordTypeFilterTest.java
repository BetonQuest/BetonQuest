package org.betonquest.betonquest.logger.filter;

import org.betonquest.betonquest.logger.BetonQuestLogRecord;
import org.junit.jupiter.api.Test;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test for the {@link LogRecordTypeFilterTest}.
 */
class LogRecordTypeFilterTest {
    @Test
    void testInstanceOf() {
        final Filter filter = new LogRecordTypeFilter(BetonQuestLogRecord.class);
        assertTrue(filter.isLoggable(new BetonQuestLogRecord(Level.INFO, "", "", null)), "BetonQuestLogRecord should be loggable");
    }

    @Test
    void testNotInstanceOf() {
        final Filter filter = new LogRecordTypeFilter(BetonQuestLogRecord.class);
        assertFalse(filter.isLoggable(new LogRecord(Level.INFO, "")), "LogRecord should not be loggable");
    }
}
