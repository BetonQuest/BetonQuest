package org.betonquest.betonquest.lib.version;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
class UnspecifiedVersionTest {

    private static final Set<String> ANY_STRING = Set.of(
            "1.0", "2.0-dev", "2.2-asdasz77+45", "literally anything", "123i?´d#+as",
            "1.0.", "3.0.0-dev-1.1", "1.0.0b", "3.0.0-dev.1",
            "1.2.5-DEV-1.1", "2.2.a", "2.2.0-SNAPSHOT", "2.2.0-SNAPSHOT-1", "2.2.0-alpha.1", "1.1.1.1", "1.0.0+dev-55",
            "1.0.0-DEV", "1.2.5-DEV", "2.0.0-DEV", "2.2.0-DEV", "3.0.0-DEV", "1.0.0-dev-", "2.2.0-rc.1",
            "3.0.0-UNOFFICIAL", "2.2.-DEV-50", "2.2.0-dev-1", "1.0.0-DEV-ARTIFACT-Wolf2323/Betonquest-55",
            "1.10", "1.0.0", "1.2.5", "2.0.0", "2.2.0", "3.0.0",
            "1.0.0-DEV-55", "1.2.5-DEV-23", "2.0.0-DEV-334", "2.2.0-DEV-918", "3.0.0-DEV-1",
            "5.0.0-DEV-UNOFFICIAL", "1.0.0-DEV-ARTIFACT-34541", "1.0.0-DEV-ARTIFACT-Wolf2323-Betonquest-55");

    private static Stream<Arguments> anyVersion() {
        return ANY_STRING.stream().map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("anyVersion")
    void any_non_empty_string_is_valid_for_unspecified_version(final String versionString) {
        assertDoesNotThrow(() -> VersionParser.parse(DefaultVersionType.UNDEFINED_VERSION, versionString));
    }

    @Test
    void empty_string_is_invalid_for_unspecified_version() {
        assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(DefaultVersionType.UNDEFINED_VERSION, ""));
    }
}
