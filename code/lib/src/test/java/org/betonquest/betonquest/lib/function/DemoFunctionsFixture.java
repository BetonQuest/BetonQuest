package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.api.function.FunctionProvider;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.lib.function.assignment.BooleanSourceAssignment;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;
import org.betonquest.betonquest.lib.function.assignment.NumericInvertedFunctionAssignment;
import org.betonquest.betonquest.lib.function.assignment.StringSourceAssignment;
import org.betonquest.betonquest.lib.function.token.DefaultTokens;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@SuppressWarnings({"PMD.ShortMethodName", "PMD.AvoidFieldNameMatchingMethodName", "PMD.UseUtilityClass",
        "PMD.AvoidLiteralsInIfCondition", "PMD.MutableStaticState", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
class DemoFunctionsFixture {

    private static final MathFunction MAX = new MathFunction() {
        @Override
        public FunctionDefinition definition() {
            return assignments -> Map.of();
        }

        @Override
        public FunctionExpression expression() {
            return (f, a) -> evaluate(f, new ArrayList<>(a.values()));
        }

        @Override
        public FunctionAssignment evaluate(final FunctionProvider functions, final List<FunctionAssignment> assignments) throws QuestException {
            //max function implementation
            return assignments.stream().reduce(assignments.get(0), (a, b) -> a.asNumber().doubleValue() > b.asNumber().doubleValue() ? a : b);
        }
    };

    protected static FunctionProvider functionProvider;

    private static Set<Arguments> validFunctionTokens;

    private static Set<Arguments> validFunctionInputs;

    private static Set<Arguments> invalidFunctionInputs;

    private static Set<Arguments> validFunctions;

    @BeforeAll
    static void setupBeforeAll() throws QuestException {
        validFunctionTokens = new HashSet<>();
        validFunctionInputs = new HashSet<>();
        invalidFunctionInputs = new HashSet<>();
        validFunctions = new HashSet<>();
        functionProvider = mock(FunctionProvider.class);
        when(functionProvider.getSubRoutine(eq("max"))).thenReturn(MAX);
        when(functionProvider.getFunction(eq("max"))).thenReturn(MAX);
        generate();
    }

    private static FunctionToken fob() {
        return DefaultTokens.FUNCTION_OPEN_BRACKET;
    }

    private static FunctionToken fcb() {
        return DefaultTokens.FUNCTION_CLOSE_BRACKET;
    }

    private static FunctionToken dob() {
        return DefaultTokens.DEFINITION_OPEN_BRACKET;
    }

    private static FunctionToken dcb() {
        return DefaultTokens.DEFINITION_CLOSE_BRACKET;
    }

    private static FunctionToken s() {
        return new FunctionToken(FunctionTokenType.SPACE, " ");
    }

    private static FunctionToken n(final String number) {
        return new FunctionToken(FunctionTokenType.NUMBER, number);
    }

    private static FunctionToken q(final String qualifier) {
        return new FunctionToken(FunctionTokenType.QUALIFIER, qualifier);
    }

    private static FunctionToken op(final String operator) {
        return new FunctionToken(FunctionTokenType.OPERATOR, operator);
    }

    private static FunctionToken id(final String identifier) {
        return new FunctionToken(FunctionTokenType.IDENTIFIER, identifier);
    }

    private static FunctionToken str(final String string) {
        return new FunctionToken(FunctionTokenType.STRING, string);
    }

    private static List<List<FunctionAssignment>> generateBooleanAssignments(final int arguments) {
        if (arguments == 1) {
            return List.of(List.of(new BooleanSourceAssignment(true)), List.of(new BooleanSourceAssignment(false)));
        }
        if (arguments == 2) {
            return List.of(List.of(new BooleanSourceAssignment(true), new BooleanSourceAssignment(true)),
                    List.of(new BooleanSourceAssignment(true), new BooleanSourceAssignment(false)),
                    List.of(new BooleanSourceAssignment(false), new BooleanSourceAssignment(true)),
                    List.of(new BooleanSourceAssignment(false), new BooleanSourceAssignment(false)));
        }
        throw new IllegalArgumentException("Invalid number of arguments: %d".formatted(arguments));
    }

    private static List<List<FunctionAssignment>> generateAssignments(final int arguments) {
        if (arguments == 1) {
            final List<List<FunctionAssignment>> assignments = new ArrayList<>();
            for (int i = -10; i < 10; i += 2) {
                assignments.add(List.of(new NumberSourceAssignment(i)));
            }
            for (int i = -100; i < 100; i += 10) {
                assignments.add(List.of(new NumberSourceAssignment(Math.random() * i)));
            }
            return assignments;
        }
        if (arguments == 2) {
            final List<List<FunctionAssignment>> assignments = new ArrayList<>();
            for (int i = -10; i < 10; i++) {
                for (int j = -10; j < 10; j++) {
                    assignments.add(List.of(new NumberSourceAssignment(i), new NumberSourceAssignment(j)));
                }
            }
            for (int i = -120; i < 120; i += 10) {
                for (int j = -120; j < 120; j += 10) {
                    assignments.add(List.of(new NumberSourceAssignment(i * i / 99.9), new NumberSourceAssignment(j / 5.5)));
                }
            }
            return assignments;
        }
        throw new IllegalArgumentException("Invalid number of arguments: %d".formatted(arguments));
    }

    private static void valid(final String input, final List<FunctionToken> tokens, final Function<List<FunctionAssignment>, FunctionAssignment> function, final int arguments) {
        validFunctionTokens.add(Arguments.of(input, tokens));
        validFunctionInputs.add(Arguments.of(input));
        Stream.concat(generateAssignments(arguments).stream(), generateBooleanAssignments(arguments).stream())
                .forEach(args -> validFunctions.add(Arguments.of(input, function, args)));
    }

    private static void valid(final String input, final List<FunctionToken> tokens) {
        validFunctionTokens.add(Arguments.of(input, tokens));
        validFunctionInputs.add(Arguments.of(input));
    }

    private static void invalidFunc(final String input, final List<FunctionToken> tokens) {
        validFunctionTokens.add(Arguments.of(input, tokens));
        invalidFunctionInputs.add(Arguments.of(input));
    }

    protected static Stream<Arguments> validFunctionTokens() {
        return validFunctionTokens.stream();
    }

    protected static Stream<Arguments> validFunctionInputs() {
        return validFunctionInputs.stream();
    }

    protected static Stream<Arguments> invalidFunctionInputs() {
        return invalidFunctionInputs.stream();
    }

    protected static Stream<Arguments> validFunctions() {
        return validFunctions.stream();
    }

    private static void generate() {
        valid("f(x) = x",
                List.of(q("f"), dob(), q("x"), dcb(), s(), op("="), s(), q("x")),
                list -> list.get(0),
                1);
        valid("f(x) = 5",
                List.of(q("f"), dob(), q("x"), dcb(), s(), op("="), s(), n("5")),
                list -> new NumberSourceAssignment(5),
                1);
        valid("f(x,y) = x * y",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), dcb(), s(), op("="),
                        s(), q("x"), s(), op("*"), s(), q("y")),
                list -> new NumberSourceAssignment(list.get(0).asNumber().doubleValue() * list.get(1).asNumber().doubleValue()),
                2);
        valid("f(x,y) = x + y * x",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), dcb(), s(), op("="),
                        s(), q("x"), s(), op("+"), s(), q("y"), s(), op("*"), s(), q("x")),
                list -> new NumberSourceAssignment(list.get(0).asNumber().doubleValue() + (list.get(1).asNumber().doubleValue() * list.get(0).asNumber().doubleValue())),
                2);
        valid("f(a,b) = a < b",
                List.of(q("f"), dob(), q("a"), op(","), q("b"), dcb(), s(), op("="),
                        s(), q("a"), s(), op("<"), s(), q("b")),
                list -> new BooleanSourceAssignment(list.get(0).asNumber().doubleValue() < list.get(1).asNumber().doubleValue()),
                2);
        valid("f(a,b) = a >= b",
                List.of(q("f"), dob(), q("a"), op(","), q("b"), dcb(), s(), op("="),
                        s(), q("a"), s(), op(">"), op("="), s(), q("b")),
                list -> new BooleanSourceAssignment(list.get(0).asNumber().doubleValue() >= list.get(1).asNumber().doubleValue()),
                2);
        valid("f(a,b) = a <= b",
                List.of(q("f"), dob(), q("a"), op(","), q("b"), dcb(), s(), op("="),
                        s(), q("a"), s(), op("<"), op("="), s(), q("b")),
                list -> new BooleanSourceAssignment(list.get(0).asNumber().doubleValue() <= list.get(1).asNumber().doubleValue()),
                2);
        valid("f(a,b) = a != b",
                List.of(q("f"), dob(), q("a"), op(","), q("b"), dcb(), s(), op("="),
                        s(), q("a"), s(), op("!"), op("="), s(), q("b")),
                list -> new BooleanSourceAssignment(list.get(0).asNumber().doubleValue() != list.get(1).asNumber().doubleValue()),
                2);
        valid("f(a,b) = a = b",
                List.of(q("f"), dob(), q("a"), op(","), q("b"), dcb(), s(), op("="),
                        s(), q("a"), s(), op("="), s(), q("b")),
                list -> new BooleanSourceAssignment(list.get(0).asNumber().doubleValue() == list.get(1).asNumber().doubleValue()),
                2);
        valid("f(x,y) = x > y ? x : y",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), dcb(), s(), op("="),
                        s(), q("x"), s(), op(">"), s(), q("y"), s(),
                        op("?"), s(), q("x"), s(), op(":"), s(), q("y")),
                list -> list.get(0).asNumber().doubleValue() > list.get(1).asNumber().doubleValue() ? list.get(0) : list.get(1),
                2);
        valid("f(x,y) = x>y ? x*2 : y+2",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), dcb(), s(), op("="),
                        s(), q("x"), op(">"), q("y"), s(), op("?"), s(),
                        q("x"), op("*"), n("2"), s(), op(":"), s(), q("y"), op("+"), n("2")),
                list -> list.get(0).asNumber().doubleValue() > list.get(1).asNumber().doubleValue()
                        ? new NumberSourceAssignment(list.get(0).asNumber().doubleValue() * 2) : new NumberSourceAssignment(list.get(1).asNumber().doubleValue() + 2),
                2);
        valid("f(x,y) = x>y | !x",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), dcb(), s(), op("="),
                        s(), q("x"), op(">"), q("y"), s(), op("|"), s(), op("!"), q("x")),
                list -> new BooleanSourceAssignment(list.get(0).asNumber().doubleValue() > list.get(1).asNumber().doubleValue() || !list.get(0).asBoolean()),
                2);
        valid("f(x) = -(-x)",
                List.of(q("f"), dob(), q("x"), dcb(), s(), op("="),
                        s(), op("-"), fob(), op("-"), q("x"), fcb()),
                list -> list.get(0),
                1);
        valid("f(x:5) = x",
                List.of(q("f"), dob(), q("x"), op(":"), n("5"), dcb(), s(), op("="), s(), q("x")),
                list -> list.get(0),
                1);
        valid("f(x) = {func}(x) + 5",
                List.of(q("f"), dob(), q("x"), dcb(), s(), op("="), s(), id("{func}"),
                        fob(), q("x"), fcb(), s(), op("+"), s(), n("5")));
        valid("f(x)=\"test\"", List.of(q("f"), fob(), q("x"), fcb(), op("="), str("\"test\"")),
                list -> new StringSourceAssignment("test"), 1);
        valid("f(x)=max(x,10)",
                List.of(q("f"), dob(), q("x"), dcb(), op("="), q("max"), fob(), q("x"),
                        op(","), n("10"), fcb()),
                list -> list.get(0).asNumber().doubleValue() > 10 ? list.get(0) : new NumberSourceAssignment(10),
                1);
        valid("f(x)={max}(x,10)",
                List.of(q("f"), dob(), q("x"), dcb(), op("="), id("{max}"), fob(), q("x"),
                        op(","), n("10"), fcb()),
                list -> list.get(0).asNumber().doubleValue() > 10 ? list.get(0) : new NumberSourceAssignment(10),
                1);
        valid("f(x)=x>0|x<0|x>=0|x=0|x<=0|x!=0",
                List.of(q("f"), dob(), q("x"), dcb(), op("="), q("x"), op(">"), n("0"), op("|"),
                        q("x"), op("<"), n("0"), op("|"), q("x"), op(">"), op("="), n("0"), op("|"),
                        q("x"), op("="), n("0"), op("|"), q("x"), op("<"), op("="), n("0"), op("|"),
                        q("x"), op("!"), op("="), n("0")),
                list -> new BooleanSourceAssignment(true), 1);
        valid("f(x:true)=true&true&false",
                List.of(q("f"), dob(), q("x"), op(":"), q("true"), dcb(), op("="),
                        q("true"), op("&"), q("true"), op("&"), q("false")),
                list -> new BooleanSourceAssignment(false), 1);
        valid("f(x)=-x^2^2",
                List.of(q("f"), dob(), q("x"), dcb(), op("="), op("-"), q("x"), op("^"), n("2"), op("^"), n("2")),
                list -> new NumericInvertedFunctionAssignment(new NumberSourceAssignment(Math.pow(list.get(0).asNumber().doubleValue(), 4))),
                1);
        valid("f(x,y)=x%10/y",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), dcb(), op("="),
                        q("x"), op("%"), n("10"), op("/"), q("y")),
                list -> new NumberSourceAssignment(list.get(0).asNumber().doubleValue() % 10 / list.get(1).asNumber().doubleValue()), 2);
        valid("f(x,y:10)=x+y-2",
                List.of(q("f"), dob(), q("x"), op(","), q("y"), op(":"), n("10"), dcb(), op("="),
                        q("x"), op("+"), q("y"), op("-"), n("2")),
                list -> new NumberSourceAssignment(list.get(0).asNumber().doubleValue() + 8), 1);
        valid("f(x:5) = x-5",
                List.of(q("f"), dob(), q("x"), op(":"), n("5"), dcb(), s(), op("="),
                        s(), q("x"), op("-"), n("5")),
                list -> new NumberSourceAssignment(list.get(0).asNumber().doubleValue() - 5),
                1);

        invalidFunc("f(x)", List.of(q("f"), dob(), q("x"), dcb()));
        invalidFunc("f(x)=(", List.of(q("f"), dob(), q("x"), dcb(), op("="), fob()));
        invalidFunc("5", List.of(n("5")));
        invalidFunc("f({x})", List.of(q("f"), dob(), id("{x}"), dcb()));
        invalidFunc("f(x) =", List.of(q("f"), dob(), q("x"), dcb(), s(), op("=")));
        invalidFunc("f(x:{x})=x", List.of(q("f"), dob(), q("x"), op(":"), id("{x}"), dcb(), op("="), q("x")));
        invalidFunc("f(x) = {func}x",
                List.of(q("f"), dob(), q("x"), dcb(), s(), op("="), s(), id("{func}"), q("x")));
        invalidFunc("f(x) = {func}",
                List.of(q("f"), dob(), q("x"), dcb(), s(), op("="), s(), id("{func}")));
        invalidFunc("f", List.of(q("f")));
        invalidFunc("f(x)=y", List.of(q("f"), dob(), q("x"), dcb(), op("="), q("y")));
    }
}
