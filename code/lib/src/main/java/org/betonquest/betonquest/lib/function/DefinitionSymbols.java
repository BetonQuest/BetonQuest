package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.function.FunctionAssignment;
import org.betonquest.betonquest.api.function.FunctionDefinition;
import org.betonquest.betonquest.lib.function.assignment.DefaultFallbackAssignment;
import org.betonquest.betonquest.lib.function.assignment.StringSourceAssignment;
import org.betonquest.betonquest.lib.function.symbols.NonTerminalSymbol;
import org.betonquest.betonquest.lib.function.symbols.NonTerminalSymbolBuilder;
import org.betonquest.betonquest.lib.function.token.DefaultTokens;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Defines the symbols used to parse function definitions.
 * <pre>
 * {@code
 * <value> ::= <number> | <string> | 'true' | 'false'
 * <parameter> ::= <qualifier> | <qualifier> : <value>
 * <parameter-list> ::= <parameter> | <parameter> , <parameter-list>
 * <definition> ::= <qualifier> | <qualifier>(<parameter-list>)
 * }
 * </pre>
 *
 * @since 3.1.0
 */
@SuppressWarnings("PMD.ConstantsInInterface")
public interface DefinitionSymbols {

    /**
     * Represents a raw value.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionAssignment> VALUE = new NonTerminalSymbolBuilder<FunctionAssignment>()
            .branch(FunctionTokenType.NUMBER, scanner -> new StringSourceAssignment(scanner.consume().containedValue()))
            .branch(FunctionTokenType.STRING, scanner -> new StringSourceAssignment(scanner.consume().containedValue()))
            .branch(scanner -> scanner.peek(DefaultTokens.KEYWORD_TRUE) || scanner.peek(DefaultTokens.KEYWORD_FALSE),
                    scanner -> new StringSourceAssignment(scanner.consume().containedValue()))
            .parse(scanner -> {
                throw new QuestException("Unexpected token '%s' while parsing function definition.".formatted(scanner.peek().type()));
            })
            .build();

    /**
     * Represents a function definition parameter with or without a default value.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<Map.Entry<String, FunctionAssignment>> PARAMETER = new NonTerminalSymbolBuilder<Map.Entry<String, FunctionAssignment>>()
            .prefix(FunctionTokenType.QUALIFIER)
            .parse(scanner -> {
                final FunctionToken name = scanner.consume();
                FunctionAssignment value = new DefaultFallbackAssignment();
                if (scanner.peek(DefaultTokens.DEFINITION_PARAMETER_DEFAULT_SEPARATOR)) {
                    scanner.consume(DefaultTokens.DEFINITION_PARAMETER_DEFAULT_SEPARATOR, "Missing default value separator for parameter.");
                    value = VALUE.parse(scanner);
                }
                return Map.entry(name.containedValue(), value);
            })
            .build();

    /**
     * Represents a list of function definition parameters.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<List<Map.Entry<String, FunctionAssignment>>> PARAMETER_LIST = new NonTerminalSymbol<>() {
        @Override
        public boolean matches(final TokenScanner scanner) {
            return PARAMETER.matches(scanner);
        }

        @Override
        public List<Map.Entry<String, FunctionAssignment>> parse(final TokenScanner scanner) throws QuestException {
            final List<Map.Entry<String, FunctionAssignment>> parameters = new LinkedList<>();
            parameters.add(PARAMETER.parse(scanner));
            if (scanner.peek().type() == FunctionTokenType.OPERATOR) {
                scanner.consume(DefaultTokens.DEFINITION_PARAMETER_LIST_SEPARATOR, "Expected separator in parameter list of function definition.");
                parameters.addAll(PARAMETER_LIST.parse(scanner));
            }
            return parameters;
        }
    };

    /**
     * Represents the entire function definition.
     *
     * @since 3.1.0
     */
    NonTerminalSymbol<FunctionDefinition> DEFINITION = new NonTerminalSymbolBuilder<FunctionDefinition>()
            .parse(scanner -> {
                scanner.consume(FunctionTokenType.QUALIFIER, "Missing function name qualifier.");
                scanner.consume(DefaultTokens.DEFINITION_OPEN_BRACKET, "Missing opening bracket for function definition.");
                final List<Map.Entry<String, FunctionAssignment>> parameterList = PARAMETER_LIST.parse(scanner);
                scanner.consume(DefaultTokens.DEFINITION_CLOSE_BRACKET, "Missing closing bracket for function definition.");
                return assignments -> {
                    final Map<String, FunctionAssignment> result = new HashMap<>(parameterList.size());
                    for (int i = 0; i < parameterList.size(); i++) {
                        final Map.Entry<String, FunctionAssignment> entry = parameterList.get(i);
                        result.put(entry.getKey(), assignments.size() > i ? assignments.get(i) : entry.getValue());
                    }
                    return result;
                };
            })
            .build();
}
