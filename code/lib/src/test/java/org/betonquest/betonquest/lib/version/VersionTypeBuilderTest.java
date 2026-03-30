package org.betonquest.betonquest.lib.version;

import org.betonquest.betonquest.api.version.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
class VersionTypeBuilderTest {

    @Test
    void simple_builder_builds() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite().any("any");
        final DefaultVersionType anyType = builder.build();

        assertEquals(1, anyType.tokenGroups().size(), "Should have exactly one token group");
        final Version version = VersionParser.parse(anyType, "absolutely anything should be parseable by this type");
        assertEquals("absolutely anything should be parseable by this type", version.getNamedElement("any").orElse(null), "Should be able to parse anything");
    }

    @Test
    void simple_number_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite().number("number");
        final DefaultVersionType versionType = builder.build();

        assertDoesNotThrow(() -> VersionParser.parse(versionType, "12345"), "Should be able to parse a version with a number");
        assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(versionType, "1.5"), "Should not be able to parse a version with a decimal");
    }

    @Test
    void defaulting_to_correct_value() {
        final String defaultValue = "SNAPSHOT";
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite().number("version")
                .opt().dash().finite().any("any", defaultValue);
        final DefaultVersionType versionType = builder.build();
        final Version version = VersionParser.parse(versionType, "1");
        assertEquals(defaultValue, version.getNamedElement("any").orElse(null), "Default value should be used");
        assertDoesNotThrow(() -> VersionParser.parse(versionType, "1-anything"), "Should be able to parse a version with a dash and a default value");
    }

    @Test
    void semantic_type_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder()
                .finite().number("major")
                .opt()
                .dot().finite().number("minor")
                .opt()
                .dot().finite().number("patch");
        final DefaultVersionType versionType = builder.build();
        final String[] valid = {"1", "1.2", "1.2.3"};
        final String[] invalid = {"", ".", "1.", "1.2.", "1.2.3.", "1.2.3.4"};
        for (final String version : valid) {
            assertDoesNotThrow(() -> VersionParser.parse(versionType, version), "Should be able to parse a valid version");
        }
        for (final String version : invalid) {
            assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(versionType, version), "Should not be able to parse an invalid version");
        }
    }

    @Test
    void match_exact_type_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite().exact("version", "1.2.3");
        final DefaultVersionType versionType = builder.build();
        final Version version = VersionParser.parse(versionType, "1.2.3");
        assertEquals("1.2.3", version.getNamedElement("version").orElse(null), "Should be able to parse a version with an exact match");
        assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(versionType, "1.2.4"), "Should not be able to parse a different version");
    }

    @Test
    void match_exact_optional_type_builder() {
        final String absenceDefault = "SNAPSHOT";
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite().number("number")
                .opt().plus().finite().exact("snapshot", absenceDefault, absenceDefault);
        final DefaultVersionType versionType = builder.build();
        final Version version = VersionParser.parse(versionType, "45");
        assertEquals(absenceDefault, version.getNamedElement("snapshot").orElse(null), "Should be able to find default value from exact match");
        assertDoesNotThrow(() -> VersionParser.parse(versionType, "45+" + absenceDefault), "Should be able to parse a version with an exact match");
    }

    @Test
    void fail_on_impossible_type() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().any("any").finite().any("any2");
        final DefaultVersionType versionType = builder.build();

        assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(versionType, "12345"), "Should not be able to parse anything");
        assertThrows(IllegalArgumentException.class, () -> VersionParser.parse(versionType, "literally anything"), "Should not be able to parse anything");
    }

    @Test
    void fail_on_empty_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder();
        assertThrows(IllegalStateException.class, builder::build, "Should not be able to build an empty builder");
    }

    @Test
    void fail_on_non_finite_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().any("any");
        assertThrows(IllegalStateException.class, builder::build, "Should not be able to build a non-finite builder");
    }

    @Test
    void fail_on_finite_empty_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite();
        assertThrows(IllegalStateException.class, builder::build, "Should not be able to build a finite empty builder");
    }

    @Test
    void fail_on_empty_optional_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder();
        assertThrows(IllegalStateException.class, builder::opt, "Should not be able to open an optional token on an empty builder");
    }

    @Test
    void fail_on_empty_optional_builder_with_token_before() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().any("any").opt();
        assertThrows(IllegalStateException.class, builder::build, "Should not be able to build a builder with an optional token as last token");
    }

    @Test
    void fail_on_closing_empty_optional_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().any("any").opt();
        assertThrows(IllegalStateException.class, builder::opt, "Should not be able to close an empty optional token");
    }

    @Test
    void fail_on_closing_non_empty_non_finite_optional_builder() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().any("any").opt().any("any2").opt();
        assertThrows(IllegalStateException.class, builder::build, "Should not be able to build a builder with an empty optional group");
    }

    @Test
    void fail_on_marking_a_token_as_finite_twice() {
        final VersionTypeBuilder builder = DefaultVersionType.builder().finite();
        assertThrows(IllegalStateException.class, builder::finite, "Should not be able to mark a token as finite twice");
    }
}
