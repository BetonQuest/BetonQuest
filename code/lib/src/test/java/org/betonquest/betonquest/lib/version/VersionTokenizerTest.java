package org.betonquest.betonquest.lib.version;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VersionTokenizerTest {

    private static Stream<Arguments> validTokens() {
        final String numRegex = "^([0-9]+)";
        return Stream.of(
                Arguments.of("1.2.3-dev-55", new String[]{numRegex, "^\\.", numRegex, "^\\.", numRegex,
                        "^-", "dev|DEV", "^-", numRegex}),
                Arguments.of("1.2.3", new String[]{numRegex, "^\\.", numRegex, "^\\.", numRegex}),
                Arguments.of("1", new String[]{numRegex}),
                Arguments.of("1.2b11", new String[]{numRegex, "^\\.", numRegex, "^b", numRegex})
        );
    }

    private static Stream<Arguments> invalidTokens() {
        final String badRegex = "^([a-z]+)";
        return Stream.of(
                Arguments.of("1.2.3-dev-55", badRegex),
                Arguments.of("1.2.3", badRegex),
                Arguments.of("1", badRegex),
                Arguments.of("1.2b11", badRegex)
        );
    }

    @ParameterizedTest
    @MethodSource("validTokens")
    void valid_token_consumption_can_consume(final String input, final String... patterns) {
        final VersionTokenizer tokenizer = new VersionTokenizer(input);
        for (final String pattern : patterns) {
            final Pattern regex = Pattern.compile(pattern);
            assertCanConsume(tokenizer);
            assertConsumption(tokenizer, regex);
        }
    }

    @ParameterizedTest
    @MethodSource("validTokens")
    void valid_token_consumption_has_token(final String input, final String... patterns) {
        final VersionTokenizer tokenizer = new VersionTokenizer(input);
        for (final String pattern : patterns) {
            final Pattern regex = Pattern.compile(pattern);
            assertToken(tokenizer, regex, true);
            assertConsumption(tokenizer, regex);
        }
    }

    @ParameterizedTest
    @MethodSource("invalidTokens")
    void invalid_token_consumption(final String input, final String pattern) {
        final VersionTokenizer tokenizer = new VersionTokenizer(input);
        final Pattern regex = Pattern.compile(pattern);
        assertCanConsume(tokenizer);
        assertToken(tokenizer, regex, false);
    }

    @ParameterizedTest
    @MethodSource("invalidTokens")
    void invalid_token_consumption_fail(final String input, final String pattern) {
        final VersionTokenizer tokenizer = new VersionTokenizer(input);
        final Pattern regex = Pattern.compile(pattern);
        assertFailedConsumption(tokenizer, regex);
    }

    private void assertFailedConsumption(final VersionTokenizer tokenizer, final Pattern pattern) {
        assertThrows(IllegalArgumentException.class, () -> tokenizer.consume(pattern), "Pattern '%s' should not be able to be consumed from tokenizer '%s'".formatted(pattern, tokenizer));
    }

    private void assertConsumption(final VersionTokenizer tokenizer, final Pattern pattern) {
        assertTrue(pattern.asMatchPredicate().test(tokenizer.consume(pattern)), "Pattern '%s' not consumed correctly from tokenizer '%s'".formatted(pattern, tokenizer));
    }

    private void assertCanConsume(final VersionTokenizer tokenizer) {
        assertTrue(tokenizer.canConsume(), "Tokenizer '%s' should be able to consume tokens".formatted(tokenizer));
    }

    private void assertToken(final VersionTokenizer tokenizer, final Pattern pattern, final boolean expected) {
        assertEquals(expected, tokenizer.has(pattern), String.format("Pattern %s not found in %s", pattern, tokenizer));
    }
}
