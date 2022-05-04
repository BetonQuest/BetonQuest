package org.betonquest.betonquest.modules.logger.custom.chat;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test for the {@link PlayerPackageFilter}.
 */
@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
class PlayerPackageFilterTest {
    /**
     * Filter for a package.
     */
    private final static String FILTER1 = "example";
    /**
     * Filter for a sub package of {@link PlayerPackageFilterTest#FILTER1}.
     */
    private final static String FILTER2 = FILTER1 + "-package";
    /**
     * Filter for a sub package of {@link PlayerPackageFilterTest#FILTER2}.
     */
    private final static String FILTER3 = FILTER2 + "-sub";

    /**
     * Default constructor.
     */
    public PlayerPackageFilterTest() {
        // Empty
    }

    @Test
    void testAddRemoveGetFilter() {
        final PlayerFilter playerFilter = new PlayerPackageFilter();
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();

        playerFilter.addFilter(uuid1, FILTER2, Level.WARNING);
        playerFilter.addFilter(uuid2, FILTER2, Level.WARNING);
        playerFilter.addFilter(uuid2, FILTER3, Level.WARNING);

        assertEquals(List.of(FILTER2), playerFilter.getFilters(uuid1), "The list if filters should match");
        assertEquals(List.of(FILTER2, FILTER3), playerFilter.getFilters(uuid2), "The list if filters should match");
        assertEquals(Set.of(uuid1, uuid2), playerFilter.getUUIDs(), "Players should be in the list");

        playerFilter.removeFilter(uuid1, FILTER2);
        playerFilter.removeFilter(uuid2, FILTER2);
        playerFilter.removeFilter(uuid2, FILTER3);
        assertTrue(playerFilter.getFilters(uuid1).isEmpty(), "Filters should be empty");
        assertTrue(playerFilter.getFilters(uuid2).isEmpty(), "Filters should be empty");
    }

    @Test
    void testSimpleFilter() {
        final PlayerFilter playerFilter = new PlayerPackageFilter();
        final UUID uuid = UUID.randomUUID();
        playerFilter.addFilter(uuid, FILTER2, Level.INFO);
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.FINE));
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.INFO));
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.WARNING));
        assertMatch(false, playerFilter.filter(uuid, FILTER2, Level.FINE));
        assertMatch(true, playerFilter.filter(uuid, FILTER2, Level.INFO));
        assertMatch(true, playerFilter.filter(uuid, FILTER2, Level.WARNING));
        assertMatch(false, playerFilter.filter(uuid, FILTER3, Level.FINE));
        assertMatch(false, playerFilter.filter(uuid, FILTER3, Level.INFO));
        assertMatch(false, playerFilter.filter(uuid, FILTER3, Level.WARNING));
    }

    @Test
    void testSubPackageFilter() {
        final PlayerFilter playerFilter = new PlayerPackageFilter();
        final UUID uuid = UUID.randomUUID();
        playerFilter.addFilter(uuid, FILTER2 + "*", Level.INFO);
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.FINE));
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.INFO));
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.WARNING));
        assertMatch(false, playerFilter.filter(uuid, FILTER2, Level.FINE));
        assertMatch(true, playerFilter.filter(uuid, FILTER2, Level.INFO));
        assertMatch(true, playerFilter.filter(uuid, FILTER2, Level.WARNING));
        assertMatch(false, playerFilter.filter(uuid, FILTER3, Level.FINE));
        assertMatch(true, playerFilter.filter(uuid, FILTER3, Level.INFO));
        assertMatch(true, playerFilter.filter(uuid, FILTER3, Level.WARNING));
    }

    @Test
    void testStarFilter() {
        final PlayerFilter playerFilter = new PlayerPackageFilter();
        final UUID uuid = UUID.randomUUID();
        playerFilter.addFilter(uuid, "*", Level.INFO);
        assertMatch(false, playerFilter.filter(uuid, FILTER1, Level.FINE));
        assertMatch(true, playerFilter.filter(uuid, FILTER1, Level.INFO));
        assertMatch(true, playerFilter.filter(uuid, FILTER1, Level.WARNING));
        assertMatch(false, playerFilter.filter(uuid, FILTER2, Level.FINE));
        assertMatch(true, playerFilter.filter(uuid, FILTER2, Level.INFO));
        assertMatch(true, playerFilter.filter(uuid, FILTER2, Level.WARNING));
        assertMatch(false, playerFilter.filter(uuid, FILTER3, Level.FINE));
        assertMatch(true, playerFilter.filter(uuid, FILTER3, Level.INFO));
        assertMatch(true, playerFilter.filter(uuid, FILTER3, Level.WARNING));
    }

    private void assertMatch(final boolean expected, final boolean actual) {
        final String message = expected ? "Should match" : "Should not match";
        assertEquals(expected, actual, message);
    }
}
