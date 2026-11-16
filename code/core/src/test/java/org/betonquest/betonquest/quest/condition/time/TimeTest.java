package org.betonquest.betonquest.quest.condition.time;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link Time}.
 */
class TimeTest {

    @Test
    void time_is_before() {
        final Time time = new Time(1, 0);
        assertTrue(time.isBeforeOrSame(new Time(2, 20)), "1:00 should be before 2:20");
    }

    @Test
    void time_is_same() {
        final Time time = new Time(3, 0);
        assertTrue(time.isBeforeOrSame(new Time(3, 0)), "3:00 should be the same as 3:00");
    }

    @Test
    void time_is_not_before() {
        final Time time = new Time(1, 0);
        assertFalse(time.isBeforeOrSame(new Time(0, 20)), "1:00 should not be before 0:20");
    }

    @Test
    void time_is_after() {
        final Time time = new Time(4, 0);
        assertTrue(time.isAfterOrSame(new Time(3, 55)), "4:00 should be after 3:55");
    }

    @Test
    void time_is_same_after() {
        final Time time = new Time(4, 0);
        assertTrue(time.isAfterOrSame(new Time(4, 0)), "4:00 should be the same as 4:00");
    }

    @Test
    void time_is_not_after() {
        final Time time = new Time(4, 0);
        assertFalse(time.isAfterOrSame(new Time(4, 20)), "4:00 should not be after 4:20");
    }
}
