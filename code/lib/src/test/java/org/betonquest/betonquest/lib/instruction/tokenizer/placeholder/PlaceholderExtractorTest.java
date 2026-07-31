package org.betonquest.betonquest.lib.instruction.tokenizer.placeholder;

import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderExtractorTest {

    private static Stream<Arguments> validInstructions() {
        return Stream.of(
                Arguments.of("Simple! This is text with a %placeholder.value% and suffix", List.of("%placeholder.value%")),
                Arguments.of("Quoting! %ph.{%sub.value%}.extra% is a test.", List.of("%ph.{%sub.value%}.extra%")),
                Arguments.of("Escaping! %some.\\{in brackets\\}% is a test.", List.of("%some.\\{in brackets\\}%")),
                Arguments.of("Ignoring! This contains no {placeholder} 100\\%!", List.of()),
                Arguments.of("Nesting! %some.{nested {%deeply.{even more}.{and {more}%}}}% and that's a test.", List.of("%some.{nested {%deeply.{even more}.{and {more}%}}}%")),
                Arguments.of("Multiple! %one% and %two% and %more.complex.{with quote}%", List.of("%one%", "%two%", "%more.complex.{with quote}%")),
                Arguments.of("Complex! %one%\\% and %two%\\% = {%more.complex.{with quote}%}", List.of("%one%", "%two%", "%more.complex.{with quote}%"))
        );
    }

    private static Stream<String> invalidInstructions() {
        return Stream.of(
                "prefix %placeholder suffix",
                "prefix %placeholder\\% suffix",
                "bad quoting %ph.{%sub.value%.extra%",
                "bad quoting %ph.{%sub.value.extra%",
                "bad quoting %ph.{%sub.value%\\}.extra%"
        );
    }

    @ParameterizedTest
    @MethodSource("validInstructions")
    void strings_are_tokenized_correctly(final String instruction, final List<String> expected) throws TokenizerException {
        final Tokenizer tokenizer = new PlaceholderExtractor();
        final Token[] parsed = tokenizer.tokens(instruction);

        final String[] mappedParsed = Stream.of(parsed).map(Token::value).toArray(String[]::new);
        final String[] mappedExpected = expected.toArray(String[]::new);
        assertArrayEquals(mappedExpected, mappedParsed, "The tokenized instruction should match the expected for instruction: '%s' != actual: '%s'".formatted(List.of(expected), Arrays.asList(parsed)));
    }

    @ParameterizedTest
    @MethodSource("invalidInstructions")
    void invalid_strings_throw_tokenizer_exception(final String instruction) {
        final Tokenizer tokenizer = new PlaceholderExtractor();
        assertThrows(TokenizerException.class, () -> tokenizer.tokens(instruction), "Expected tokenizing to fail for instruction: " + instruction);
    }
}
