package org.betonquest.betonquest.lib.instruction.tokenizer.quoting;

import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.Tokenizer;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.placeholder.PlaceholderExtractor;
import org.betonquest.betonquest.lib.instruction.tokenizer.placeholder.PlaceholderExtractorSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderTokenizerTest {

    private static Stream<Arguments> validInstructions() {
        return Stream.of(
                Arguments.of("constant.myValue", tokens(t("constant"), t("myValue"))),
                Arguments.of("test.{quoted part}", tokens(t("test"), t("quoted part"))),
                Arguments.of("test.{%constant.{nested.value}%}", tokens(t("test"), t("%constant.{nested.value}%"))),
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
                Arguments.of("ph.{{%constant.vault%}_eco_balance}", tokens(t("ph"), children(t("%constant.vault%"), t("_eco_balance")))),
                Arguments.of("test.{etwas.mit.Quote}.{und einem Placeholder %test.placeholder%}.ende",
                        tokens(t("test"), t("etwas.mit.Quote"), t("und einem Placeholder %test.placeholder%"), t("ende"))),
                Arguments.of("{quoted.first}.value", tokens(t("quoted.first"), t("value"))),
                Arguments.of("test.{{prefix}.suffix}", tokens(t("test"), children(t("prefix"), t(".suffix")))),
                Arguments.of("test.{{prefix}.suffix}.another",
                        tokens(t("test"), children(t("prefix"), t(".suffix")), t("another"))),
                Arguments.of("test.{%ph.{quoted}.value%}", tokens(t("test"), t("%ph.{quoted}.value%"))),
                Arguments.of("test.{%ph.{%inner.{deep.value}%}%}", tokens(t("test"), t("%ph.{%inner.{deep.value}%}%"))),
                Arguments.of("test.{%ph.{}%}", tokens(t("test"), t("%ph.{}%"))),
                Arguments.of("test.{%some.\\{in brackets\\}%}", tokens(t("test"), t("%some.\\{in brackets\\}%"))),
                Arguments.of("test.{prefix %ph.{a.b}% suffix}", tokens(t("test"), t("prefix %ph.{a.b}% suffix"))),
                Arguments.of("test.{%one.{a}% and %two.{b}%}", tokens(t("test"), t("%one.{a}% and %two.{b}%"))),
                Arguments.of("test.{{%ph.{a}%}_suffix}", tokens(t("test"), children(t("%ph.{a}%"), t("_suffix")))),
                Arguments.of("test.{%ph%{grouped}}", tokens(t("test"), children(t("%ph%"), t("grouped")))),
                Arguments.of("test.{100% sure}", tokens(t("test"), t("100% sure"))),
                Arguments.of("test.{escaped \\% {grouped}}", tokens(t("test"), children(t("escaped \\% "), t("grouped")))),
                Arguments.of("test.{\\%no.placeholder\\%}", tokens(t("test"), t("\\%no.placeholder\\%"))),
                Arguments.of("test.\\%unquoted\\%", tokens(t("test"), t("\\%unquoted\\%"))),
                Arguments.of("test.{%ph.\\%escaped%}", tokens(t("test"), t("%ph.\\%escaped%"))),
                Arguments.of("test..value", tokens(t("test"), t("value"))),
                Arguments.of(".test.value.", tokens(t("test"), t("value"))),
                Arguments.of("test.{}.value", tokens(t("test"), t("value"))),
                Arguments.of("test.{{prefix}\\}suffix}", tokens(t("test"), children(t("prefix"), t("}suffix")))),
                Arguments.of("test.{a{b{c}d}e}", tokens(t("test"), children(t("a"), children(t("b"), t("c"), t("d")), t("e")))),
                Arguments.of("test.with\\.dot", tokens(t("test"), t("with.dot"))),
                Arguments.of("some.\\{in brackets\\}", tokens(t("some"), t("{in brackets}"))),
                Arguments.of("function.{demo.functions.test}.{%point.reputation.amount%}.{%eval.function.{%constant.myconstant%}%}",
                        tokens(t("function"), t("demo.functions.test"), t("%point.reputation.amount%"), t("%eval.function.{%constant.myconstant%}%")))
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
                "test.{something{else}}{}}.further",
                "test.{{prefix}.suffix",
                "test.{{prefix}.suffix}}",
                "test.{{prefix}\\}",
                "test.prefix{value}",
                "test.prefix{value}.another",
                "test.prefix}value",
                "test.value}",
                "test.trailing\\",
                "test.{%ph.{a}%",
                "test.{%ph.{a%}",
                "test.{%ph.{a}",
                "test.{%ph.\\}"
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

    @Test
    void deeply_nested_tokens_resolve_to_joined_value() throws TokenizerException {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.PLACEHOLDER);
        final Token[] parsed = tokenizer.tokens("test.{a{b{c}d}e}");
        assertEquals("abcde", parsed[1].resolveValue(), "Nested tokens should resolve to the joined value");
    }

    @Test
    void escaped_placeholder_markers_are_not_extracted_as_placeholder() throws TokenizerException {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.PLACEHOLDER);
        final Tokenizer extractor = new PlaceholderExtractor(PlaceholderExtractorSettings.ONLY_PLACEHOLDERS);
        final Token[] parsed = tokenizer.tokens("test.{\\%no.placeholder\\%}");
        assertEquals(0, extractor.tokens(parsed[1].resolveValue()).length, "Escaped markers should not form a placeholder");
    }

    @Test
    void nested_placeholders_can_be_tokenized_recursively() throws TokenizerException {
        final Tokenizer tokenizer = new QuotingTokenizer(QuotingTokenizerSettings.PLACEHOLDER);
        final Tokenizer extractor = new PlaceholderExtractor(PlaceholderExtractorSettings.ONLY_PLACEHOLDERS);
        final List<String> visited = new ArrayList<>();
        String instruction = "outer.{%middle.{prefix %inner.{a.b}% suffix}%}";
        while (true) {
            final Token[] parsed = tokenizer.tokens(instruction);
            visited.add(Stream.of(parsed).map(Token::resolveValue).collect(Collectors.joining("|")));
            final Token[] placeholders = extractor.tokens(parsed[parsed.length - 1].resolveValue());
            if (placeholders.length == 0) {
                break;
            }
            final String placeholder = placeholders[0].value();
            instruction = placeholder.substring(1, placeholder.length() - 1);
        }
        assertEquals(List.of("outer|%middle.{prefix %inner.{a.b}% suffix}%", "middle|prefix %inner.{a.b}% suffix", "inner|a.b"),
                visited, "Each placeholder level should keep the syntax of its nested placeholders");
    }
}
