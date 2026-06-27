package org.betonquest.betonquest.lib.function;

import com.google.common.base.Suppliers;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionExpression;
import org.betonquest.betonquest.api.function.MathFunction;
import org.betonquest.betonquest.lib.function.assignment.BooleanInvertedFunctionAssignment;
import org.betonquest.betonquest.lib.function.assignment.BooleanSourceAssignment;
import org.betonquest.betonquest.lib.function.assignment.NumberSourceAssignment;
import org.betonquest.betonquest.lib.function.assignment.NumericInvertedFunctionAssignment;
import org.betonquest.betonquest.lib.function.assignment.StringSourceAssignment;
import org.betonquest.betonquest.lib.function.symbols.NonTerminalSymbol;
import org.betonquest.betonquest.lib.function.symbols.NonTerminalSymbolBuilder;
import org.betonquest.betonquest.lib.function.token.DefaultTokens;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines the symbols used to parse function expressions.
 * <pre>
 * {@code
 * <expr> ::= <ternary>
 *
 * <ternary> ::= <disjunction> | <disjunction> '?' <expr> ':' <ternary>
 *
 * <disjunction> ::= <conjunction> {'|' <conjunction>}
 *
 * <conjunction> ::= <comparison> {'&' <comparison>}
 *
 * <compare-symbol> ::= '<' | '>' | '<=' | '>=' | '=' | '!='
 * <comparison> ::= <add> | <add> <compare-symbol> <add>
 *
 * <sum> ::= '+' | '-'
 * <add> ::= <product> {<sum> <product>}
 *
 * <factor> ::= '*' | '/' | '%'
 * <product> ::= <unary> {<factor> <unary>}
 *
 * <unary> ::= <exponent> | '-' <unary> | '!' <unary>
 *
 * <exponent> ::= <primary> | <primary> '^' <exponent>
 *
 * <primary> ::= <value> | <function-call> | '(' <expr> ')'
 *
 * <expr-list> ::= <expr> {',' <expr>}
 * <function-call> ::= <identifier> '(' <expr-list> ')' | <qualifier> '(' <expr-list> ')'
 * <value> ::= <number> | <string> | 'true' | 'false' | <qualifier>
 * }
 * </pre>
 *
 * @since 3.1.0
 */
@SuppressWarnings("PMD.ConstantsInInterface")
public interface ExpressionSymbols {

    /**
     * Represents a raw number value.
     *
     * @see FunctionTokenType#NUMBER
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionAssignment> VALUE_NUMBER = new NonTerminalSymbolBuilder<FunctionAssignment>()
            .prefix(FunctionTokenType.NUMBER)
            .parse(scanner -> new StringSourceAssignment(scanner.consume().containedValue()))
            .build();

    /**
     * Represents a raw boolean value.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionAssignment> VALUE_BOOLEAN = new NonTerminalSymbolBuilder<FunctionAssignment>()
            .prefix((scanner, pos) -> scanner.peek(DefaultTokens.KEYWORD_TRUE) || scanner.peek(DefaultTokens.KEYWORD_FALSE))
            .parse(scanner -> new BooleanSourceAssignment(Boolean.parseBoolean(scanner.consume().containedValue())))
            .build();

    /**
     * Represents a raw string value.
     *
     * @see FunctionTokenType#STRING
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionAssignment> VALUE_STRING = new NonTerminalSymbolBuilder<FunctionAssignment>()
            .prefix(FunctionTokenType.STRING)
            .parse(scanner -> new StringSourceAssignment(scanner.consume().containedValue()))
            .build();

    /**
     * Represents the value of a variable.
     *
     * @see FunctionTokenType#QUALIFIER
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> VALUE_QUALIFIER = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix(FunctionTokenType.QUALIFIER)
            .parse(scanner -> {
                final String variableName = scanner.consume().containedValue();
                return (f, a) -> {
                    final FunctionAssignment assignment = a.get(variableName);
                    if (assignment == null) {
                        throw new QuestException("Error while parsing function. Variable '%s' is not defined.".formatted(variableName));
                    }
                    return assignment;
                };
            })
            .build();

    /**
     * Represents {@code <value>} from the grammar.
     *
     * @see #VALUE_NUMBER
     * @see #VALUE_STRING
     * @see #VALUE_QUALIFIER
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> VALUE = new NonTerminalSymbolBuilder<FunctionExpression>()
            .branch(VALUE_NUMBER::matches, scanner -> {
                final FunctionAssignment parsedNumber = VALUE_NUMBER.parse(scanner);
                return (f, a) -> parsedNumber;
            })
            .branch(VALUE_STRING::matches, scanner -> {
                final FunctionAssignment parsedString = VALUE_STRING.parse(scanner);
                return (f, a) -> parsedString;
            })
            .branch(VALUE_BOOLEAN::matches, scanner -> {
                final FunctionAssignment parsedBoolean = VALUE_BOOLEAN.parse(scanner);
                return (f, a) -> parsedBoolean;
            })
            .branch(VALUE_QUALIFIER)
            .parse(scanner -> {
                throw new QuestException("Error while parsing function. Expected grammar token 'value' but got '%s'.".formatted(scanner.peek()));
            })
            .build();

    /**
     * Represents {@code <expr-list>} from the grammar.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<List<FunctionExpression>> EXPRESSION_LIST = new NonTerminalSymbolBuilder<List<FunctionExpression>>()
            .parse(scanner -> {
                final FunctionExpression expression = ExpressionSymbols.EXPRESSION.get().parse(scanner);
                final List<FunctionExpression> expressions = new ArrayList<>();
                expressions.add(expression);
                while (scanner.peek(DefaultTokens.FUNCTION_PARAMETER_SEPARATOR)) {
                    scanner.consume(DefaultTokens.FUNCTION_PARAMETER_SEPARATOR, "Missing comma between function parameters.");
                    expressions.add(ExpressionSymbols.EXPRESSION.get().parse(scanner));
                }
                return expressions;
            })
            .build();

    /**
     * Represents one part{@code <qualifier>(<expr-list>)} from the grammar.
     *
     * @see #EXPRESSION_LIST
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> SUBROUTINE_CALL = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix(FunctionTokenType.QUALIFIER)
            .prefix(DefaultTokens.FUNCTION_OPEN_BRACKET)
            .parse(scanner -> {
                final String qualifier = scanner.consume().containedValue();
                scanner.consume(DefaultTokens.FUNCTION_OPEN_BRACKET, "Missing opening bracket for function call with qualifier '%s'.".formatted(qualifier));
                final List<FunctionExpression> expressions = EXPRESSION_LIST.parse(scanner);
                scanner.consume(DefaultTokens.FUNCTION_CLOSE_BRACKET, "Missing closing bracket for function call with qualifier '%s'.".formatted(qualifier));
                return (functions, assignments) -> {
                    final MathFunction mathFunction = functions.getSubRoutine(qualifier);
                    final List<FunctionAssignment> assignmentList = new ArrayList<>();
                    for (final FunctionExpression expr : expressions) {
                        final FunctionAssignment evaluate = expr.evaluate(functions, assignments);
                        assignmentList.add(evaluate);
                    }
                    return mathFunction.evaluate(functions, assignmentList);
                };
            })
            .build();

    /**
     * Represents one part{@code <identifier>(<expr-list>)} from the grammar.
     *
     * @see #EXPRESSION_LIST
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> FUNCTION_CALL = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix(FunctionTokenType.IDENTIFIER)
            .prefix(DefaultTokens.FUNCTION_OPEN_BRACKET)
            .parse(scanner -> {
                final String identifier = scanner.consume().containedValue();
                scanner.consume(DefaultTokens.FUNCTION_OPEN_BRACKET, "Missing opening bracket for function call with identifier '%s'.".formatted(identifier));
                final List<FunctionExpression> expressions = EXPRESSION_LIST.parse(scanner);
                scanner.consume(DefaultTokens.FUNCTION_CLOSE_BRACKET, "Missing closing bracket for function call with identifier '%s'.".formatted(identifier));
                return (functions, assignments) -> {
                    final MathFunction mathFunction = functions.getFunction(identifier);
                    final List<FunctionAssignment> assignmentList = new ArrayList<>();
                    for (final FunctionExpression expr : expressions) {
                        final FunctionAssignment evaluate = expr.evaluate(functions, assignments);
                        assignmentList.add(evaluate);
                    }
                    return mathFunction.evaluate(functions, assignmentList);
                };
            })
            .build();

    /**
     * Represents {@code <expr>} from the grammar.
     *
     * @since 3.1.0
     */
    Supplier<NonTerminalSymbol<FunctionExpression>> EXPRESSION = Suppliers.memoize(() -> new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(ExpressionSymbols.TERNARY)
            .build());

    /**
     * Represents {@code <function-call>} from the grammar.
     *
     * @see #FUNCTION_CALL
     * @see #SUBROUTINE_CALL
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> FUNCTION = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix((scanner, pos) -> scanner.peek(DefaultTokens.FUNCTION_OPEN_BRACKET, pos + 1))
            .branch(FUNCTION_CALL)
            .branch(SUBROUTINE_CALL)
            .parse(scanner -> {
                throw new QuestException("Error while parsing function. Expected grammar token 'function-call' but got '%s'.".formatted(scanner.peek()));
            })
            .build();

    /**
     * Represents a sub grammar of {@code <unary>} from the grammar.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> NOT_UNARY = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix(DefaultTokens.NOT_OPERATOR)
            .parse(scanner -> {
                scanner.consume(DefaultTokens.NOT_OPERATOR, "Missing not operator for unary not expression.");
                final FunctionExpression expression = ExpressionSymbols.UNARY.parse(scanner);
                return (f, a) -> new BooleanInvertedFunctionAssignment(expression.evaluate(f, a));
            })
            .build();

    /**
     * Represents a sub grammar of {@code <unary>} from the grammar.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> MINUS_UNARY = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix(DefaultTokens.MINUS_OPERATOR)
            .parse(scanner -> {
                scanner.consume(DefaultTokens.MINUS_OPERATOR, "Missing minus operator for unary minus expression.");
                final FunctionExpression expression = ExpressionSymbols.UNARY.parse(scanner);
                return (f, a) -> new NumericInvertedFunctionAssignment(expression.evaluate(f, a));
            })
            .build();

    /**
     * Represents {@code <product>} from the grammar.
     *
     * @see #UNARY
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> PRODUCT = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                FunctionExpression result = ExpressionSymbols.UNARY.parse(scanner);
                while (scanner.peek(DefaultTokens.MULTIPLY_OPERATOR) || scanner.peek(DefaultTokens.DIVIDE_OPERATOR) || scanner.peek(DefaultTokens.MODULO_OPERATOR)) {
                    if (scanner.peek(DefaultTokens.MULTIPLY_OPERATOR)) {
                        scanner.consume(DefaultTokens.MULTIPLY_OPERATOR, "Missing multiplication operator for product expression.");
                        final FunctionExpression first = result;
                        final FunctionExpression second = ExpressionSymbols.UNARY.parse(scanner);
                        result = (f, a) -> new NumberSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() * second.evaluate(f, a).asNumber().doubleValue());
                    }
                    if (scanner.peek(DefaultTokens.DIVIDE_OPERATOR)) {
                        scanner.consume(DefaultTokens.DIVIDE_OPERATOR, "Missing division operator for division expression.");
                        final FunctionExpression first = result;
                        final FunctionExpression second = ExpressionSymbols.UNARY.parse(scanner);
                        result = (f, a) -> new NumberSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() / second.evaluate(f, a).asNumber().doubleValue());
                    }
                    if (scanner.peek(DefaultTokens.MODULO_OPERATOR)) {
                        scanner.consume(DefaultTokens.MODULO_OPERATOR, "Missing modulo operator for modulo expression.");
                        final FunctionExpression first = result;
                        final FunctionExpression second = ExpressionSymbols.UNARY.parse(scanner);
                        result = (f, a) -> new NumberSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() % second.evaluate(f, a).asNumber().doubleValue());
                    }
                }
                return result;
            })
            .build();

    /**
     * Represents {@code <add>} from the grammar.
     *
     * @see #PRODUCT
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> ADDITION = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                FunctionExpression result = PRODUCT.parse(scanner);
                while (scanner.peek(DefaultTokens.PLUS_OPERATOR) || scanner.peek(DefaultTokens.MINUS_OPERATOR)) {
                    if (scanner.peek(DefaultTokens.PLUS_OPERATOR)) {
                        scanner.consume(DefaultTokens.PLUS_OPERATOR, "Missing plus operator for sum expression.");
                        final FunctionExpression first = result;
                        final FunctionExpression second = PRODUCT.parse(scanner);
                        result = (f, a) ->
                                new NumberSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() + second.evaluate(f, a).asNumber().doubleValue());
                    }
                    if (scanner.peek(DefaultTokens.MINUS_OPERATOR)) {
                        scanner.consume(DefaultTokens.MINUS_OPERATOR, "Missing minus operator for subtraction expression.");
                        final FunctionExpression first = result;
                        final FunctionExpression second = PRODUCT.parse(scanner);
                        result = (f, a) ->
                                new NumberSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() - second.evaluate(f, a).asNumber().doubleValue());
                    }
                }
                return result;
            })
            .build();

    /**
     * Represents {@code <ternary>} from the grammar.
     *
     * @see #DISJUNCTION
     * @see #EXPRESSION
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> TERNARY = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                final FunctionExpression conditionExpression = ExpressionSymbols.DISJUNCTION.parse(scanner);
                if (scanner.peek(DefaultTokens.PRIMARY_TERNARY_OPERATOR)) {
                    scanner.consume(DefaultTokens.PRIMARY_TERNARY_OPERATOR, "Missing ternary operator for ternary expression.");
                    final FunctionExpression primary = EXPRESSION.get().parse(scanner);
                    scanner.consume(DefaultTokens.SECONDARY_TERNARY_OPERATOR, "Missing colon operator for ternary expression.");
                    final FunctionExpression alternative = ExpressionSymbols.TERNARY.parse(scanner);
                    return (f, a) -> {
                        final FunctionAssignment ternaryCondition = conditionExpression.evaluate(f, a);
                        return ternaryCondition.asBoolean() ? primary.evaluate(f, a) : alternative.evaluate(f, a);
                    };
                }
                return conditionExpression;
            })
            .build();

    /**
     * Represents a part of {@code <primary>} from the grammar.
     *
     * @see #EXPRESSION
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> BRACKET = new NonTerminalSymbolBuilder<FunctionExpression>()
            .prefix(FunctionTokenType.OPEN_BRACKET)
            .parse(scanner -> {
                scanner.consume(FunctionTokenType.OPEN_BRACKET, "Missing opening bracket for bracketed expression.");
                final FunctionExpression expression = EXPRESSION.get().parse(scanner);
                scanner.consume(FunctionTokenType.CLOSE_BRACKET, "Missing closing bracket for bracketed expression.");
                return expression;
            })
            .build();

    /**
     * Represents {@code <primary>} from the grammar.
     *
     * @see #BRACKET
     * @see #FUNCTION
     * @see #VALUE
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> PRIMARY = new NonTerminalSymbolBuilder<FunctionExpression>()
            .branch(BRACKET)
            .branch(FUNCTION)
            .branch(VALUE)
            .parse(scanner -> {
                throw new QuestException("Error while parsing function. Expected grammar token 'primary' but got '%s'.".formatted(scanner.peek()));
            })
            .build();

    /**
     * Represents {@code <comparison>} from the grammar.
     *
     * @see #ADDITION
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> COMPARISON = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                final FunctionExpression first = ADDITION.parse(scanner);
                if (scanner.peek(DefaultTokens.LESS_OPERATOR) || scanner.peek(DefaultTokens.GREATER_OPERATOR) || scanner.peek(DefaultTokens.EQUAL_OPERATOR)
                        || (scanner.peek(DefaultTokens.NOT_OPERATOR) && scanner.peek(DefaultTokens.EQUAL_OPERATOR, 1))) {
                    final FunctionToken firstToken = scanner.consume();
                    final StringBuilder comparisonToken = new StringBuilder(firstToken.containedValue());
                    if (!firstToken.equals(DefaultTokens.EQUAL_OPERATOR) && scanner.peek(DefaultTokens.EQUAL_OPERATOR)) {
                        comparisonToken.append(scanner.consume().containedValue());
                    }
                    final FunctionExpression second = ADDITION.parse(scanner);
                    return switch (comparisonToken.toString()) {
                        case "<" -> (f, a) ->
                                new BooleanSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() < second.evaluate(f, a).asNumber().doubleValue());
                        case ">" -> (f, a) ->
                                new BooleanSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() > second.evaluate(f, a).asNumber().doubleValue());
                        case "=" -> (f, a) ->
                                new BooleanSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() == second.evaluate(f, a).asNumber().doubleValue());
                        case "<=" -> (f, a) ->
                                new BooleanSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() <= second.evaluate(f, a).asNumber().doubleValue());
                        case ">=" -> (f, a) ->
                                new BooleanSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() >= second.evaluate(f, a).asNumber().doubleValue());
                        case "!=" -> (f, a) ->
                                new BooleanSourceAssignment(first.evaluate(f, a).asNumber().doubleValue() != second.evaluate(f, a).asNumber().doubleValue());
                        default ->
                                throw new QuestException("Error while parsing function. Unknown comparison operator '%s'.".formatted(comparisonToken));
                    };
                }
                return first;
            })
            .build();

    /**
     * Represents {@code <exponent>} from the grammar.
     *
     * @see #PRIMARY
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> EXPONENT = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                final FunctionExpression comparison = PRIMARY.parse(scanner);
                if (scanner.peek(DefaultTokens.POWER_OPERATOR)) {
                    scanner.consume(DefaultTokens.POWER_OPERATOR, "Missing power operator for exponent expression.");
                    final FunctionExpression exponent = ExpressionSymbols.EXPONENT.parse(scanner);
                    return (f, a) -> new NumberSourceAssignment(Math.pow(comparison.evaluate(f, a).asNumber().doubleValue(), exponent.evaluate(f, a).asNumber().doubleValue()));
                }
                return comparison;
            })
            .build();

    /**
     * Represents {@code <conjunction>} from the grammar.
     *
     * @see #COMPARISON
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> CONJUNCTION = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                FunctionExpression result = COMPARISON.parse(scanner);
                while (scanner.peek(DefaultTokens.CONJUNCTION_OPERATOR)) {
                    scanner.consume(DefaultTokens.CONJUNCTION_OPERATOR, "Missing conjunction operator in conjunction expression.");
                    final FunctionExpression firstExpression = result;
                    final FunctionExpression secondExpression = COMPARISON.parse(scanner);
                    result = (f, a) -> new BooleanSourceAssignment(firstExpression.evaluate(f, a).asBoolean() && secondExpression.evaluate(f, a).asBoolean());
                }
                return result;
            })
            .build();

    /**
     * Represents {@code <disjunction>} from the grammar.
     *
     * @see #CONJUNCTION
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> DISJUNCTION = new NonTerminalSymbolBuilder<FunctionExpression>()
            .parse(scanner -> {
                FunctionExpression result = CONJUNCTION.parse(scanner);
                while (scanner.peek(DefaultTokens.DISJUNCTION_OPERATOR)) {
                    scanner.consume(DefaultTokens.DISJUNCTION_OPERATOR, "Missing disjunction operator in disjunction expression.");
                    final FunctionExpression firstExpression = result;
                    final FunctionExpression secondExpression = CONJUNCTION.parse(scanner);
                    result = (f, a) -> new BooleanSourceAssignment(firstExpression.evaluate(f, a).asBoolean() || secondExpression.evaluate(f, a).asBoolean());
                }
                return result;
            })
            .build();

    /**
     * Represents {@code <unary>} from the grammar.
     *
     * @see #EXPONENT
     * @see #MINUS_UNARY
     * @see #NOT_UNARY
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionExpression> UNARY = new NonTerminalSymbolBuilder<FunctionExpression>()
            .branch(MINUS_UNARY)
            .branch(NOT_UNARY)
            .parse(EXPONENT)
            .build();
}
