package org.betonquest.betonquest.util.math;

import org.betonquest.betonquest.api.QuestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link LegacyMathConverter}.
 */
class LegacyMathConverterTest {

    private static Stream<Arguments> validExpressions() {
        return Stream.concat(Stream.of(
                Arguments.of("100*(15-point.reputation.amount)", "f(a) = 100*(15-a)", List.of("point.reputation.amount")),
                Arguments.of("objective.kill_zombies.left/objective.kill_zombies.total*100~2", "f(a,b) = round(a/b*100,2)",
                        List.of("objective.kill_zombies.left", "objective.kill_zombies.total")),
                Arguments.of("3+2*6-8", "f(a) = 3+2*6-8", List.of()),
                Arguments.of("|x-y|+[x]", "f(a,b) = abs(a-b)+(a)", List.of("x", "y")),
                Arguments.of("x", "f(a) = a", List.of("x")),
                Arguments.of("x+y*z/w%v^u", "f(a,b,c,d,e,f) = a+b*c/d%e^f", List.of("x", "y", "z", "w", "v", "u")),
                Arguments.of("x*x+x", "f(a) = a*a+a", List.of("x")),
                Arguments.of("x+y-x", "f(a,b) = a+b-a", List.of("x", "y")),
                Arguments.of("ph:Player_level*2", "f(a) = a*2", List.of("ph:Player_level")),
                Arguments.of("var!bang+var=qu+var#rav+var&more+variable's+var?not+var123", "f(a,b,c,d,e,f,g) = a+b+c+d+e+f+g",
                        List.of("var!bang", "var=qu", "var#rav", "var&more", "variable's", "var?not", "var123")),
                Arguments.of("{back\\\\slash}", "f(a) = a", List.of("back\\slash")),
                Arguments.of("{closing{curly\\}brace}", "f(a) = a", List.of("closing{curly}brace")),
                Arguments.of("{math.calc:5+var}*2", "f(a) = a*2", List.of("math.calc:5+var")),
                Arguments.of("{x}+x", "f(a) = a+a", List.of("x")),
                Arguments.of("((x+1)*2)", "f(a) = ((a+1)*2)", List.of("x")),
                Arguments.of("[x]", "f(a) = (a)", List.of("x")),
                Arguments.of("[(x)]", "f(a) = ((a))", List.of("x")),
                Arguments.of("|x|", "f(a) = abs(a)", List.of("x")),
                Arguments.of("-|x|", "f(a) = -abs(a)", List.of("x")),
                Arguments.of("|-3|-|-7|", "f(a) = abs(-3)-abs(-7)", List.of()),
                Arguments.of("|1-(|x|)|", "f(a) = abs(1-(abs(a)))", List.of("x")),
                Arguments.of("x~2", "f(a) = round(a,2)", List.of("x")),
                Arguments.of("10-3.03*x~1", "f(a) = round(10-3.03*a,1)", List.of("x")),
                Arguments.of("(x~2)*4", "f(a) = (round(a,2))*4", List.of("x")),
                Arguments.of("x~-2", "f(a) = round(a,-2)", List.of("x")),
                Arguments.of("x~7-5", "f(a) = round(a,7-5)", List.of("x")),
                Arguments.of("x~1~2", "f(a) = round(round(a,1),2)", List.of("x")),
                Arguments.of("x^3~2", "f(a) = round(a^3,2)", List.of("x")),
                Arguments.of("|x~1|", "f(a) = abs(round(a,1))", List.of("x")),
                Arguments.of("x~y", "f(a,b) = round(a,b)", List.of("x", "y")),
                Arguments.of("-x", "f(a) = -a", List.of("x")),
                Arguments.of("7--x", "f(a) = 7--a", List.of("x")),
                Arguments.of("x*-2", "f(a) = a*-2", List.of("x")),
                Arguments.of("-(x-y)", "f(a,b) = -(a-b)", List.of("x", "y")),
                Arguments.of("-{x}", "f(a) = -a", List.of("x")),
                Arguments.of("x^-2", "f(a) = a^-2", List.of("x")),
                Arguments.of("2^3^2", "f(a) = 2^3^2", List.of()),
                Arguments.of(" x + 2 *\ty ", "f(a,b) = a+2*b", List.of("x", "y")),
                Arguments.of("1.5e3+.5+2.", "f(a) = 1.5e3+.5+2.", List.of()),
                Arguments.of("NaN+Infinity", "f(a) = NaN+Infinity", List.of())
        ), manyPlaceholders());
    }

    private static Stream<Arguments> invalidExpressions() {
        return Stream.of(
                Arguments.of("", "Missing calculation"),
                Arguments.of("-", "Invalid calculation (negation missing value)"),
                Arguments.of("*64", "Invalid calculation (operator missing first value)"),
                Arguments.of("--x", "Invalid calculation (operator missing first value)"),
                Arguments.of("423*", "Invalid calculation (operator missing second value)"),
                Arguments.of("423**64", "Invalid calculation (doubled operators)"),
                Arguments.of("()", "Invalid calculation (empty parenthesis)"),
                Arguments.of("(())", "Invalid calculation (empty parenthesis)"),
                Arguments.of("||", "Invalid calculation (empty absolute value)"),
                Arguments.of("(846", "Invalid calculation (unbalanced parenthesis)"),
                Arguments.of("865)", "Invalid calculation (unbalanced parenthesis)"),
                Arguments.of("[(653])", "Invalid calculation (parenthesis / brackets mismatch)"),
                Arguments.of("|(653|)", "Invalid calculation (unbalanced absolute value)"),
                Arguments.of("{invalid", "Invalid calculation (unbalanced curly brace)"),
                Arguments.of("invalid}", "Invalid calculation (unbalanced curly brace)"),
                Arguments.of("{}", "Invalid calculation (empty placeholder)"),
                Arguments.of("7(2+4)", "Invalid calculation (missing operator)"),
                Arguments.of("(2+4)x", "Invalid calculation (missing operator)"),
                Arguments.of("2var", "Invalid calculation (missing operator)")
        );
    }

    private static Stream<Arguments> manyPlaceholders() {
        final List<String> placeholders = IntStream.range(0, 28).mapToObj(index -> "ph" + index).toList();
        final String expression = String.join("+", placeholders);
        final String parameters = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,aa,ab";
        final String body = String.join("+", parameters.split(","));
        return Stream.of(Arguments.of(expression, "f(" + parameters + ") = " + body, placeholders),
                Arguments.of(expression + "+ph0", "f(" + parameters + ") = " + body + "+a", placeholders),
                Arguments.of(IntStream.range(0, 27).mapToObj(index -> "ph" + index).collect(Collectors.joining("*")),
                        "f(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,aa) = a*b*c*d*e*f*g*h*i*j*k*l*m*n*o*p*q*r*s*t*u*v*w*x*y*z*aa",
                        placeholders.subList(0, 27)));
    }

    @ParameterizedTest
    @MethodSource("validExpressions")
    void converts_legacy_expression(final String expression, final String definition, final List<String> placeholders) throws QuestException {
        final Map.Entry<String, List<String>> result = new LegacyMathConverter().extractFunctionDefinition(expression);
        assertEquals(definition, result.getKey(), "Definition should match");
        assertEquals(placeholders, result.getValue(), "Placeholders should match");
    }

    @ParameterizedTest
    @MethodSource("invalidExpressions")
    void throws_on_invalid_expression(final String expression, final String message) {
        final LegacyMathConverter converter = new LegacyMathConverter();
        final QuestException exception = assertThrows(QuestException.class, () -> converter.extractFunctionDefinition(expression));
        assertEquals(message, exception.getMessage(), "Exception message should match");
    }
}
