package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BetonQuestVersionTest {

    @Test
    void dev_versions_are_higher_than_stable_versions() {
        final Version higherVersion = BetonQuestVersion.parse("2.0.0-DEV-1");
        final Version lowerVersion = BetonQuestVersion.parse("1.0.0");
        assertEquals(1, higherVersion.compareTo(lowerVersion), "Version '%s' should be higher than '%s'".formatted(higherVersion, lowerVersion));
    }

    @Test
    void dev_versions_are_lower_than_their_stable_counterpart() {
        final Version lowerVersion = BetonQuestVersion.parse("1.0.0-DEV-1");
        final Version higherVersion = BetonQuestVersion.parse("1.0.0");
        assertEquals(1, higherVersion.compareTo(lowerVersion), "Version '%s' should be higher than '%s'".formatted(higherVersion, lowerVersion));
    }

    @Nested
    class Order {

        private static final String[] ORDERED_VERSIONS = {
                "1.0.0",
                "1.0.1",
                "1.1.0-DEV-2",
                "1.1.0-DEV-25",
                "1.1.0-DEV-934",
                "1.1.0",
                "1.1.1",
                "1.2.0-DEV-UNOFFICIAL",
                "1.2.0-DEV-ARTIFACT-Betonquest/Betonquest-1436",
                "1.2.0-DEV-212",
                "1.2.0",
                "1.2.1",
                "1.3.0",
                "1.3.1-DEV-212",
                "1.3.1",
                "1.3.6",
                "1.4.0",
        };

        private Version[] versions;

        @BeforeEach
        void setup() {
            versions = new Version[ORDERED_VERSIONS.length];
            for (int i = 0; i < ORDERED_VERSIONS.length; i++) {
                versions[i] = BetonQuestVersion.parse(ORDERED_VERSIONS[i]);
            }
        }

        @Test
        void version_are_in_perfect_order() {
            for (int i = 0; i < ORDERED_VERSIONS.length - 1; i++) {
                final Version current = versions[i];
                final Version next = versions[i + 1];
                assertEquals(-1, current.compareTo(next), "Version '%s' should be less than '%s'".formatted(current, next));
            }
        }

        @Test
        void version_are_in_perfect_order_reversed() {
            for (int i = ORDERED_VERSIONS.length - 1; i >= 1; i--) {
                final Version current = versions[i];
                final Version next = versions[i - 1];
                assertEquals(1, current.compareTo(next), "Version '%s' should be above '%s'".formatted(current, next));
            }
        }
    }
}
