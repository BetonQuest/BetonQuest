package org.betonquest.betonquest.lib.function.token;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTokenTypeTest {

    @SuppressWarnings("PMD.LooseCoupling")
    private static EnumSet<FunctionTokenType> testedValid;

    @SuppressWarnings("PMD.LooseCoupling")
    private static EnumSet<FunctionTokenType> testedInvalid;

    private static Set<Arguments> validCasesSet;

    private static Set<Arguments> invalidCasesSet;

    @BeforeAll
    static void setup() {
        testedValid = EnumSet.noneOf(FunctionTokenType.class);
        testedInvalid = EnumSet.noneOf(FunctionTokenType.class);
        validCasesSet = new HashSet<>();
        invalidCasesSet = new HashSet<>();
        setupCases();
    }

    private static void test(final String input, final String value, final FunctionTokenType type, final String... invalidPrefixes) {
        validCasesSet.add(Arguments.of(input, value, type));
        for (final String invalidPrefix : invalidPrefixes) {
            invalidCasesSet.add(Arguments.of(invalidPrefix + input, type));
        }
    }

    private static void setupCases() {
        test("55", "55", FunctionTokenType.NUMBER, "a", "%", ">");
        test("5-5", "5", FunctionTokenType.NUMBER, "B", "&", "--");
        test(")", ")", FunctionTokenType.CLOSE_BRACKET, "(", "a", "%", ">", "B");
        test("(", "(", FunctionTokenType.OPEN_BRACKET, ")", "a", "%", ">", "B");
        test("{someID}", "someID", FunctionTokenType.IDENTIFIER, "a", "%", ">", "B", "}");
        test("{someID.withNesting}", "someID.withNesting", FunctionTokenType.IDENTIFIER, "a", "%", ">", "B", "}");
        test("{_-package>someID.withPackage}", "_-package>someID.withPackage", FunctionTokenType.IDENTIFIER, "a", "%", ">", "B", "}");
        test("+", "+", FunctionTokenType.OPERATOR, "a", "5", "Z");
        test("-", "-", FunctionTokenType.OPERATOR, "b", " ", "1", "W");
        test("/", "/", FunctionTokenType.OPERATOR);
        test("*", "*", FunctionTokenType.OPERATOR);
        test("%", "%", FunctionTokenType.OPERATOR);
        test("&", "&", FunctionTokenType.OPERATOR);
        test("|", "|", FunctionTokenType.OPERATOR);
        test(">", ">", FunctionTokenType.OPERATOR);
        test("<=", "<", FunctionTokenType.OPERATOR);
        test("!=", "!", FunctionTokenType.OPERATOR);
        test("=", "=", FunctionTokenType.OPERATOR);
        test("x", "x", FunctionTokenType.QUALIFIER, "5", "_", "-", "{");
        test("test", "test", FunctionTokenType.QUALIFIER, "6", "%", "#");
        test("my4", "my4", FunctionTokenType.QUALIFIER);
        test(" ", " ", FunctionTokenType.SPACE, "a", "%", ">", "B", "}", "(", ")");
        test("  ", "  ", FunctionTokenType.SPACE);
        test("   ", "   ", FunctionTokenType.SPACE);
        test("\"my string\"", "my string", FunctionTokenType.STRING, "a", "%", ">", "B", "}", "(", ")");
        test("\"l1ter4lly 4nyth1ng!\"", "l1ter4lly 4nyth1ng!", FunctionTokenType.STRING, "a", "%", ">", "B", "}", "(", ")");
    }

    private static Stream<Arguments> validCases() {
        return validCasesSet.stream();
    }

    private static Stream<Arguments> invalidCases() {
        return invalidCasesSet.stream();
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void test_valid_cases(final String input, final String value, final FunctionTokenType type) {
        testedValid.add(type);
        final Matcher matcher = type.getRegex().matcher(input);
        assertTrue(matcher.find(), "Token '%s' should match regex '%s'".formatted(input, type.getRegex()));
        assertEquals(value, matcher.group("value"), "Token '%s' should match value '%s'".formatted(input, value));
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void test_invalid_cases(final String input, final FunctionTokenType type) {
        testedInvalid.add(type);
        final Matcher matcher = type.getRegex().matcher(input);
        assertFalse(matcher.matches(), "Token '%s' should not match regex '%s'".formatted(input, type.getRegex()));
    }

    @Test
    void all_tokens_are_actually_tested() {
        assertIterableEquals(EnumSet.of(FunctionTokenType.INVALID), EnumSet.complementOf(testedValid), "Not all valid tokens are tested valid cases");
        assertIterableEquals(EnumSet.of(FunctionTokenType.INVALID), EnumSet.complementOf(testedInvalid), "Not all valid tokens are tested with invalid cases");
    }
}
