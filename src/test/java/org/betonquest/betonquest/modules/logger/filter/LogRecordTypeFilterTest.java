package org.betonquest.betonquest.modules.logger.filter;

import org.betonquest.betonquest.modules.logger.BetonQuestLogRecord;
import org.junit.jupiter.api.Test;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link LogRecordTypeFilterTest}.
 */
class LogRecordTypeFilterTest {
    @Test
    void testInstanceOf() {
        final Filter filter = new LogRecordTypeFilter(BetonQuestLogRecord.class);
        assertTrue(filter.isLoggable(mock(BetonQuestLogRecord.class)), "BetonQuestLogRecord should be loggable");
    }

    @Test
    void testNotInstanceOf() {
        final Filter filter = new LogRecordTypeFilter(BetonQuestLogRecord.class);
        assertFalse(filter.isLoggable(mock(LogRecord.class)), "BetonQuestLogRecord should not be loggable");
    }
}
