package org.betonquest.betonquest.logger.handler.chat;

import org.betonquest.betonquest.logger.BetonQuestLogRecord;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link PlayerPackageReceiverSelector}.
 */
class PlayerPackageReceiverSelectorTest {
    /**
     * Message for test log records.
     */
    public static final String MESSAGE = "message";

    /**
     * Filter for a package.
     */
    private static final String OUTER_PACKAGE = "example";

    /**
     * Filter for a sub package of {@link #OUTER_PACKAGE}.
     */
    private static final String MIDDLE_PACKAGE = OUTER_PACKAGE + "-package";

    /**
     * Filter for a sub package of {@link #MIDDLE_PACKAGE}.
     */
    private static final String INNER_PACKAGE = MIDDLE_PACKAGE + "-sub";

    /**
     * Plugin for test log records.
     */
    private static final String PLUGIN = "TestPlugin";

    /**
     * Uuids to be selected on match.
     */
    private final Set<UUID> uuids = Set.of(UUID.randomUUID(), UUID.randomUUID());

    private static Stream<BetonQuestLogRecord> fineLevelRecordSource() {
        return Stream.of(
                recordWith(Level.FINE, OUTER_PACKAGE),
                recordWith(Level.FINE, MIDDLE_PACKAGE),
                recordWith(Level.FINE, INNER_PACKAGE)
        );
    }

    private static Stream<BetonQuestLogRecord> outerPackageRecordSource() {
        return Stream.of(
                recordWith(Level.INFO, OUTER_PACKAGE),
                recordWith(Level.WARNING, OUTER_PACKAGE)
        );
    }

    private static Stream<BetonQuestLogRecord> middlePackageRecordSource() {
        return Stream.of(
                recordWith(Level.INFO, MIDDLE_PACKAGE),
                recordWith(Level.WARNING, MIDDLE_PACKAGE)
        );
    }

    private static Stream<BetonQuestLogRecord> innerPackageRecordSource() {
        return Stream.of(
                recordWith(Level.INFO, INNER_PACKAGE),
                recordWith(Level.WARNING, INNER_PACKAGE)
        );
    }

    private static BetonQuestLogRecord recordWith(final Level level, final String pack) {
        return new BetonQuestLogRecord(level, MESSAGE, PLUGIN, pack);
    }

    @ParameterizedTest
    @MethodSource("middlePackageRecordSource")
    void testExactMatchFilterMatches(final BetonQuestLogRecord record) {
        final PlayerPackageReceiverSelector playerFilter = new PlayerPackageReceiverSelector(uuids, Level.INFO, MIDDLE_PACKAGE);
        assertEquals(uuids, playerFilter.findReceivers(record), "There should be two uuids in the list of receivers");
    }

    @ParameterizedTest
    @MethodSource({"outerPackageRecordSource", "innerPackageRecordSource", "fineLevelRecordSource"})
    void testExactMatchFilterIgnores(final BetonQuestLogRecord record) {
        final PlayerPackageReceiverSelector playerFilter = new PlayerPackageReceiverSelector(uuids, Level.INFO, MIDDLE_PACKAGE);
        assertEquals(Collections.emptySet(), playerFilter.findReceivers(record), "Receivers should be empty");
    }

    @ParameterizedTest
    @MethodSource({"middlePackageRecordSource", "innerPackageRecordSource"})
    void testSubPackageFilterMatches(final BetonQuestLogRecord record) {
        final PlayerPackageReceiverSelector playerFilter = new PlayerPackageReceiverSelector(uuids, Level.INFO, MIDDLE_PACKAGE + "*");
        assertEquals(uuids, playerFilter.findReceivers(record), "There should be two uuids in the list of receivers");
    }

    @ParameterizedTest
    @MethodSource({"outerPackageRecordSource", "fineLevelRecordSource"})
    void testSubPackageFilterIgnores(final BetonQuestLogRecord record) {
        final PlayerPackageReceiverSelector playerFilter = new PlayerPackageReceiverSelector(uuids, Level.INFO, MIDDLE_PACKAGE + "*");
        assertEquals(Collections.emptySet(), playerFilter.findReceivers(record), "Receivers should be empty");
    }

    @ParameterizedTest
    @MethodSource({"outerPackageRecordSource", "middlePackageRecordSource", "innerPackageRecordSource"})
    void testStarFilterMatches(final BetonQuestLogRecord record) {
        final PlayerPackageReceiverSelector playerFilter = new PlayerPackageReceiverSelector(uuids, Level.INFO, "*");

        assertEquals(uuids, playerFilter.findReceivers(record), "There should be two uuids in the list of receivers");
    }

    @ParameterizedTest
    @MethodSource("fineLevelRecordSource")
    void testStarFilterIgnores(final BetonQuestLogRecord record) {
        final PlayerPackageReceiverSelector playerFilter = new PlayerPackageReceiverSelector(uuids, Level.INFO, "*");
        assertEquals(Collections.emptySet(), playerFilter.findReceivers(record), "Receivers should be empty");
    }
}
