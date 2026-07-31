package org.betonquest.betonquest.api.instruction.tokenizer.quoting;

import org.betonquest.betonquest.api.instruction.tokenizer.Token;
import org.betonquest.betonquest.api.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.api.instruction.tokenizer.TokenizerException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderTokenizerTest {

    private static Stream<Arguments> validInstructions() {
        return Stream.of(
                Arguments.of("constant.myValue", tokens(t("constant"), t("myValue"))),
                Arguments.of("test.{quoted part}", tokens(t("test"), t("quoted part"))),
                Arguments.of("test.{%constant.{nested.value}%}",
                        tokens(t("test"), children(t("%constant."), t("nested.value"), t("%")))),
                Arguments.of("test.{{prefix}value}", tokens(t("test"), children(t("prefix"), t("value")))),
                Arguments.of("test.{{prefix}{}value{}}", tokens(t("test"), children(t("prefix"), t("value")))),
                Arguments.of("test.{{prefix}{value}}", tokens(t("test"), children(t("prefix"), t("value")))),
                Arguments.of("test.{prefix{value}}", tokens(t("test"), children(t("prefix"), t("value")))),
                Arguments.of("test.{prefix{value}}.another",
                        tokens(t("test"), children(t("prefix"), t("value")), t("another"))),
                Arguments.of("test.{}.{}", tokens(t("test"))),
                Arguments.of("test.{with quoted \\{brackets\\} inside}.suffix",
                        tokens(t("test"), t("with quoted {brackets} inside"), t("suffix"))),
                Arguments.of("test.{in nested {with quoted \\{brackets\\} inside}}.suffix",
                        tokens(t("test"), children(t("in nested "), t("with quoted {brackets} inside")), t("suffix"))),
                Arguments.of("ph.{{%constant.vault%}_eco_balance}", tokens(t("ph"), children(t("%constant.vault%"), t("_eco_balance"))))
        );
    }

    private static Token children(final Token... values) {
        return new Token(null, null, List.of(values));
    }

    private static List<Token> tokens(final Token... values) {
        return List.of(values);
    }

    @SuppressWarnings("PMD.ShortMethodName")
    private static Token t(final String value) {
        return new Token(null, value);
    }

    private static Stream<String> invalidInstructions() {
        return Stream.of(
                "{",
                "{.test",
                "test.{",
                "te.{.st",
                "}.test",
                "test.}",
                "te.}.st",
                "test.{something}else",
                "test.{something{else}",
                "test.{something{else}}{}{.further",
                "test.{something{else}}{}}.further"
        );
    }

    @ParameterizedTest
    @MethodSource("validInstructions")
    void strings_are_tokenized_correctly(final String instruction, final List<Token> expected) throws TokenizerException {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.PLACEHOLDER);
        final Token[] parsed = tokenizer.tokens(instruction);

        final String[] mappedParsed = Stream.of(parsed).map(Token::toString).toArray(String[]::new);
        final String[] mappedExpected = expected.stream().map(Token::toString).toArray(String[]::new);
        assertArrayEquals(mappedExpected, mappedParsed, "The tokenized instruction should match the expected for instruction: '%s' != actual: '%s'".formatted(List.of(expected), Arrays.asList(parsed)));
    }

    @ParameterizedTest
    @MethodSource("invalidInstructions")
    void invalid_strings_throw_tokenizer_exception(final String instruction) {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.PLACEHOLDER);
        assertThrows(TokenizerException.class, () -> tokenizer.tokens(instruction), "Expected tokenizing to fail for instruction: " + instruction);
    }
}
