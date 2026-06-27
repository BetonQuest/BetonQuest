package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.lib.function.assignment.StringSourceAssignment;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DefaultMathFunctionTest extends DemoFunctionsFixture {

    private FunctionTokenizer functionTokenizer;

    private FunctionParser functionParser;

    @BeforeEach
    void setup() {
        functionTokenizer = new FunctionTokenizer();
        functionParser = new FunctionParser();
    }

    @ParameterizedTest
    @MethodSource("validFunctions")
    void validate_effective_math_function_parsing(final String input, final Function<List<FunctionAssignment>, FunctionAssignment> function, final List<FunctionAssignment> assignments) throws QuestException {
        final List<FunctionToken> tokenList = functionTokenizer.tokenize(input);
        final FunctionAssignment result = functionParser.parseMathFunction(tokenList).evaluate(functionProvider, assignments);
        final FunctionAssignment expected = function.apply(assignments);
        if (expected instanceof StringSourceAssignment) {
            assertEquals(expected.asString(), result.asString(), "Parsing function '%s' with arguments '%s' should return '%s'".formatted(input,
                    assignments.stream().map(FunctionAssignment::asString).collect(Collectors.joining(",")), expected));
        } else {
            assertEquals(expected.asNumber().doubleValue(), result.asNumber().doubleValue(), 0.000_001,
                    "Parsing function '%s' with arguments '%s' should return '%s'".formatted(input,
                            assignments.stream().map(FunctionAssignment::asString).collect(Collectors.joining(",")), expected));
        }
    }
}
