package org.betonquest.betonquest.quest.condition.time;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link TimeFrame}.
 */
class TimeFrameTest {

    @Test
    void testTimeBetween() {
        final TimeFrame timeFrame = new TimeFrame(new Time(1, 0), new Time(2, 0));
        assertTrue(timeFrame.isTimeBetween(new Time(1, 30)), "1:30 should be between 1:00 and 2:00");
    }

    @Test
    void testExactlyMidnight() {
        final TimeFrame timeFrame = new TimeFrame(new Time(24, 0), new Time(0, 0));
        assertTrue(timeFrame.isTimeBetween(new Time(0, 0)), "0:00 should be at 24:00 and 0:00");
    }

    @Test
    void testNotBetween() {
        final TimeFrame timeFrame = new TimeFrame(new Time(1, 0), new Time(2, 0));
        assertFalse(timeFrame.isTimeBetween(new Time(2, 30)), "2:30 should not be between 1:00 and 2:00");
    }
}
