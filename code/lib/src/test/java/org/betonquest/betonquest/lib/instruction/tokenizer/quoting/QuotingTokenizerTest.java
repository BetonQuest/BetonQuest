package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class QuotingTokenizerTest {

    private static Stream<Arguments> validInstructions() {
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
                Arguments.of("random esc\\ape", new String[]{"random", "esc\\ape"}),
                Arguments.of("escaped\\ space", new String[]{"escaped\\", "space"}),
                Arguments.of("\\start escaped", new String[]{"\\start", "escaped"}),
                Arguments.of("\\ start with\\  space", new String[]{"\\", "start", "with\\", "space"}),
                Arguments.of("escaped\\\nnewline", new String[]{"escaped\\", "newline"}),
                Arguments.of("\"escaped quote\\\" works\"", new String[]{"escaped quote\" works"}),
                Arguments.of("empty \"\" word", new String[]{"empty", "", "word"}),
                Arguments.of("\\ ", new String[]{"\\"}),
                Arguments.of("inn\"er\" quote", new String[]{"inn\"er\"", "quote"}),
                Arguments.of("string\"", new String[]{"string\""}),
                Arguments.of("str\"ing", new String[]{"str\"ing"}),
                Arguments.of("\\", new String[]{"\\"}),
                Arguments.of("trailing escape\\", new String[]{"trailing", "escape\\"}),
                Arguments.of("\"adjacent\"\"quotes\"", new String[]{"adjacent", "quotes"}),
                Arguments.of("\"escaped \\\\ backslash\"", new String[]{"escaped \\ backslash"}),
                Arguments.of("\"esc\\aped\"", new String[]{"escaped"}),
                Arguments.of("with.dot", new String[]{"with.dot"}),
                Arguments.of("\"quoted.dot\"", new String[]{"quoted.dot"}),
                Arguments.of("{curly}", new String[]{"{curly}"}),
                Arguments.of("in{curly}between", new String[]{"in{curly}between"}),
                Arguments.of("\"{curly} quoted\"", new String[]{"{curly} quoted"}),
                Arguments.of("{not quoted}", new String[]{"{not", "quoted}"}),
                Arguments.of("%ph.value%", new String[]{"%ph.value%"}),
                Arguments.of("\"%ph.{a b}%\"", new String[]{"%ph.{a b}%"}),
                Arguments.of("\"escaped \\% marker\"", new String[]{"escaped % marker"}),
                Arguments.of("unquoted\\%", new String[]{"unquoted\\%"})
        );
    }

    private static Stream<String> invalidInstructions() {
        return Stream.of(
                "\"",
                "\"string",
                "my \"string",
                "inner \"quo\"te",
                "\"quoted\"tail",
                "\"trailing escape\\",
                "\"open\"\""
        );
    }

    @ParameterizedTest
    @MethodSource("validInstructions")
    void strings_are_tokenized_correctly(final String instruction, final String... expected) throws TokenizerException {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.DEFAULT);
        final Token[] parsed = tokenizer.tokens(instruction);
        final String[] mapped = Stream.of(parsed).map(Token::resolveValue).toArray(String[]::new);
        assertArrayEquals(expected, mapped, "The tokenized instruction should match the expected for instruction: '%s' != '%s'".formatted(instruction, Arrays.asList(mapped)));
    }

    @ParameterizedTest
    @MethodSource("invalidInstructions")
    void invalid_strings_throw_tokenizer_exception(final String instruction) {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.DEFAULT);
        assertThrows(TokenizerException.class, () -> tokenizer.tokens(instruction), "Expected tokenizing to fail for instruction: " + instruction);
    }
}
