package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTokenizerTest extends DemoFunctionsFixture {

    private FunctionTokenizer tokenizer;

    @BeforeEach
    void setup() {
        tokenizer = new FunctionTokenizer();
    }

    @ParameterizedTest
    @MethodSource("validFunctionTokens")
    void valid_tokenization(final String input, final List<FunctionToken> expected) {
        final List<FunctionToken> tokenized = tokenizer.tokenize(input);
        assertIterableEquals(expected, tokenized, "Tokenization of '%s' should be %s".formatted(input, expected));
    }
}
