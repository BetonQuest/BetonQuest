package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test renaming of variables to constants.
 */
class VariablesRenameTest {

    private static Stream<Arguments> entriesToMigrate() {
        return Stream.of(
                Arguments.of("$foo$",
                        "%constant.foo%"
                ),
                Arguments.of("$_-_-path-to.foo$",
                        "%_-_-path-to.constant.foo%"
                ),
                Arguments.of("%math.calc:$foo$%",
                        "%math.calc:{constant.foo}%"
                ),
                Arguments.of("%math.calc:$_-_-path-to.foo$%",
                        "%math.calc:{_-_-path-to.constant.foo}%"
                ),
                Arguments.of("foo %math.calc:1+$bla$+2-$-a-b.c$% $bar$ $-a-b.c$ bar",
                        "foo %math.calc:1+{constant.bla}+2-{-a-b.constant.c}% %constant.bar% %-a-b.constant.c% bar"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("entriesToMigrate")
    void variable_rename(final String original, final String expected) {
        final VariablesRename variablesRename = new VariablesRename();
        final String actual = variablesRename.replaceGlobalVariables(original);

        assertEquals(expected, actual, "Constant is not migrated correctly");
    }
}
