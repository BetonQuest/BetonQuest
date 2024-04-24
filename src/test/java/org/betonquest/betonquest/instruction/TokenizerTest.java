package org.betonquest.betonquest.instruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {
    @ParameterizedTest
    @MethodSource("validInstructions")
    void strings_are_tokenized_correctly(final String instruction, final String... expected) throws TokenizerException {
        final Tokenizer tokenizer = new QuotingTokenizer();
        final String[] parsed = tokenizer.tokens(instruction);
        assertArrayEquals(expected, parsed, "The tokenized instruction should match the expected for instruction: " + instruction);
    }

    public static Stream<Arguments> validInstructions() {
        return Stream.of(
                Arguments.of("", new String[]{}),
                Arguments.of(" ", new String[]{}),
                Arguments.of("   \n ", new String[]{}),
                Arguments.of("something", new String[]{"something"}),
                Arguments.of("something-with-dash", new String[]{"something-with-dash"}),
                Arguments.of("  surrounding_space   ", new String[]{"surrounding_space"}),
                Arguments.of("two parts", new String[]{"two", "parts"}),
                Arguments.of("multiple  spaces", new String[]{"multiple", "spaces"}),
                Arguments.of("new\nline", new String[]{"new", "line"}),
                Arguments.of("three of parts", new String[]{"three", "of", "parts"}),
                Arguments.of("mixed  \n separator", new String[]{"mixed", "separator"}),
                Arguments.of("\"quoted\"", new String[]{"quoted"}),
                Arguments.of("\"multiple\" \"quoted\"", new String[]{"multiple", "quoted"}),
                Arguments.of("\"quoted space\"", new String[]{"quoted space"}),
                Arguments.of("\"quoted\nnewline\"", new String[]{"quoted\nnewline"}),
                Arguments.of("\"more quotes\" \"in\ninstruction\"\nstrings", new String[]{"more quotes", "in\ninstruction", "strings"}),
                Arguments.of("  \"surrounding space\"   ", new String[]{"surrounding space"}),
                Arguments.of("random esc\\ape", new String[]{"random", "escape"}),
                Arguments.of("escaped\\ space", new String[]{"escaped space"}),
                Arguments.of("\\start escaped", new String[]{"start", "escaped"}),
                Arguments.of("\\ start with\\  space", new String[]{" start", "with ", "space"}),
                Arguments.of("escaped\\\nnewline", new String[]{"escaped\nnewline"}),
                Arguments.of("\"escaped quote\\\" works\"", new String[]{"escaped quote\" works"}),
                Arguments.of("empty \"\" word", new String[]{"empty", "", "word"}),
                Arguments.of("\\ ", new String[]{" "})
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInstructions")
    void invalid_strings_throw_tokenizer_exception(final String instruction) {
        final Tokenizer tokenizer = new QuotingTokenizer();
        assertThrows(TokenizerException.class, () -> tokenizer.tokens(instruction), "Expected tokenizing to fail for instruction: " + instruction);
    }

    public static Stream<String> invalidInstructions() {
        return Stream.of(
                "\"",
                "\"string",
                "my \"string",
                "inn\"er\" quote",
                "inner \"quo\"te",
                "string\"",
                "str\"ing",
                "\\",
                "trailing escape\\"
        );
    }
}
