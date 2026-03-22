package org.betonquest.betonquest.lib.version;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VersionTypeTest {

    private static final VersionType TEST_TYPE = VersionType.builder()
            .finite().number("build")
            .build();

    private static final VersionType TEST_TYPE2 = VersionType.builder()
            .number("year")
            .dot().number("version")
            .opt()
            .plus().finite().number("build")
            .build();

    private static Stream<Arguments> types() {
        return Stream.of(
                Arguments.of(BetonQuestVersion.BETONQUEST_VERSION_TYPE, "major.minor.patch{-type-build}?{-type-UNOFFICIAL}?{-build}?", 15),
                Arguments.of(MinecraftVersion.MINECRAFT_VERSION_TYPE, "major.minor{.patch}?", 5),
                Arguments.of(TEST_TYPE, "build", 1),
                Arguments.of(TEST_TYPE2, "year.version{+build}?", 5)
        );
    }

    @ParameterizedTest
    @MethodSource("types")
    void check_token_types_build_correctly(final VersionType type, final String expected, final int expectedLength) {
        assertEquals(expected, type.toString(), "Type should be built correctly from tokens");
        assertEquals(expectedLength, type.tokens().size(), "Type should have the correct number of tokens");
    }
}
