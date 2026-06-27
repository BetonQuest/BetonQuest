package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionParserTest extends DemoFunctionsFixture {

    private FunctionTokenizer tokenizer;

    private FunctionParser parser;

    @BeforeEach
    void setup() {
        parser = new FunctionParser();
        tokenizer = new FunctionTokenizer();
    }

    @ParameterizedTest
    @MethodSource("validFunctionInputs")
    void parse_valid_functions(final String input) {
        final List<FunctionToken> tokenList = tokenizer.tokenize(input);
        final MathFunction mathFunction = assertDoesNotThrow(() -> parser.parseMathFunction(tokenList), "Parsing function '%s' should not throw an exception".formatted(input));
        assertNotNull(mathFunction, "Parsing function '%s' should not throw an exception".formatted(input));
    }

    @ParameterizedTest
    @MethodSource("invalidFunctionInputs")
    void parse_invalid_functions(final String input) {
        final List<FunctionToken> tokenList = tokenizer.tokenize(input);
        assertThrows(QuestException.class, () -> parser.parseMathFunction(tokenList).evaluate(functionProvider, List.of(new NumberSourceAssignment(5))),
                "Parsing function '%s' should throw an exception".formatted(input));
    }
}
