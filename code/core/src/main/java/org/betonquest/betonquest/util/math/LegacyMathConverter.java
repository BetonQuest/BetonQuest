package org.betonquest.betonquest.util.math;

import org.betonquest.betonquest.api.QuestException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts the argument of a legacy math placeholder into a function definition with the used placeholders.
 */
public class LegacyMathConverter {

    /**
     * Floating point regular expression without sign. It matches only the number at the start of the checked region.
     */
    private static final Pattern FP_REGEX = Pattern.compile("(?:NaN|Infinity|(?:0[xX](?:\\p{XDigit}+(?:\\.\\p{XDigit}*)?|\\.\\p{XDigit}+)[pP][+-]?\\p{Digit}+|(?:\\p{Digit}+(?:\\.\\p{Digit}*)?|\\.\\p{Digit}+)(?:[eE][+-]?\\p{Digit}+)?)[fFdD]?)");

    /**
     * Backslash escapement regular expression for removing escaping. Use {@link Matcher#replaceAll(String)} with
     * {@code "$1"} as argument.
     */
    private static final Pattern ESCAPE_REGEX = Pattern.compile("\\\\(.)");

    /**
     * Characters that end a simple placeholder besides operators.
     */
    private static final String PLACEHOLDER_END = "{([|])}";

    /**
     * Number of letters available for parameter names.
     */
    private static final int ALPHABET_SIZE = 26;

    /**
     * Create a new legacy math converter.
     */
    public LegacyMathConverter() {
    }

    /**
     * Converts the argument of a legacy math placeholder into a function definition.
     * The key of the result is the definition like {@code f(a) = 100*(15-a)},
     * the value is the list of placeholders in the order of the parameters.
     *
     * @param legacyMathPlaceholder the argument of the legacy math placeholder without {@code math.calc:}
     * @return the function definition and the used placeholders
     * @throws QuestException if the expression is invalid
     */
    public Map.Entry<String, List<String>> extractFunctionDefinition(final String legacyMathPlaceholder) throws QuestException {
        final Parser parser = new Parser(legacyMathPlaceholder.replaceAll("\\s", ""));
        final Node body = parser.parse();
        final String parameters = parser.parameters.isEmpty() ? "a" : String.join(",", parser.parameters.values());
        final StringBuilder definition = new StringBuilder("f(").append(parameters).append(") = ");
        body.render(definition);
        return Map.entry(definition.toString(), List.copyOf(parser.parameters.keySet()));
    }

    /**
     * A node of the parsed expression.
     */
    @SuppressWarnings({"PMD.ImplicitFunctionalInterface", "PMD.ShortClassName"})
    private interface Node {

        /**
         * Appends the function expression of this node.
         *
         * @param out the builder to append to
         */
        void render(StringBuilder out);
    }

    /**
     * A number in its original text.
     *
     * @param text the number text
     */
    private record NumberNode(String text) implements Node {

        @Override
        public void render(final StringBuilder out) {
            out.append(text);
        }
    }

    /**
     * A function parameter that replaces a placeholder.
     *
     * @param name the parameter name
     */
    private record ParameterNode(String name) implements Node {

        @Override
        public void render(final StringBuilder out) {
            out.append(name);
        }
    }

    /**
     * A binary operation.
     *
     * @param left     the left operand
     * @param operator the operator symbol
     * @param right    the right operand
     */
    private record BinaryNode(Node left, char operator, Node right) implements Node {

        @Override
        public void render(final StringBuilder out) {
            left.render(out);
            out.append(operator);
            right.render(out);
        }
    }

    /**
     * A parenthesis or bracket group.
     *
     * @param inner the grouped node
     */
    private record GroupNode(Node inner) implements Node {

        @Override
        public void render(final StringBuilder out) {
            out.append('(');
            inner.render(out);
            out.append(')');
        }
    }

    /**
     * A negation.
     *
     * @param inner the negated node
     */
    private record NegationNode(Node inner) implements Node {

        @Override
        public void render(final StringBuilder out) {
            out.append('-');
            inner.render(out);
        }
    }

    /**
     * An absolute value.
     *
     * @param inner the node to get the absolute value of
     */
    private record AbsNode(Node inner) implements Node {

        @Override
        public void render(final StringBuilder out) {
            out.append("abs(");
            inner.render(out);
            out.append(')');
        }
    }

    /**
     * A rounding operation.
     *
     * @param value     the value to round
     * @param precision the precision to round to
     */
    private record RoundNode(Node value, Node precision) implements Node {

        @Override
        public void render(final StringBuilder out) {
            out.append("round(");
            value.render(out);
            out.append(',');
            precision.render(out);
            out.append(')');
        }
    }

    /**
     * Recursive descent parser for a single legacy math expression.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.AvoidLiteralsInIfCondition"})
    private static final class Parser {

        /**
         * The placeholders mapped to their parameter names in order of first occurrence.
         */
        private final Map<String, String> parameters = new LinkedHashMap<>();

        /**
         * The expression without whitespace.
         */
        private final String expression;

        /**
         * The current position in the expression.
         */
        private int position;

        /**
         * The exclusive end of the currently parsed range.
         */
        private int end;

        /**
         * Whether the last consumed character was a binary operator.
         */
        private boolean afterOperator;

        /**
         * Create a new parser for the given expression.
         *
         * @param expression the expression without whitespace
         */
        private Parser(final String expression) {
            this.expression = expression;
            this.end = expression.length();
        }

        /**
         * Parses the whole expression.
         *
         * @return the root node
         * @throws QuestException if the expression is invalid
         */
        private Node parse() throws QuestException {
            return parseRange(0, expression.length());
        }

        private Node parseRange(final int start, final int rangeEnd) throws QuestException {
            final int outerEnd = end;
            position = start;
            end = rangeEnd;
            afterOperator = false;
            final Node node = parseRound();
            if (position < end) {
                throw unexpected(expression.charAt(position));
            }
            end = outerEnd;
            afterOperator = false;
            return node;
        }

        private Node parseRound() throws QuestException {
            Node node = parseAdditive();
            while (consumeOperator("~") != 0) {
                node = new RoundNode(node, parseAdditive());
            }
            return node;
        }

        private Node parseAdditive() throws QuestException {
            Node node = parseMultiplicative();
            char operator = consumeOperator("+-");
            while (operator != 0) {
                node = new BinaryNode(node, operator, parseMultiplicative());
                operator = consumeOperator("+-");
            }
            return node;
        }

        private Node parseMultiplicative() throws QuestException {
            Node node = parseUnary();
            char operator = consumeOperator("*/%");
            while (operator != 0) {
                node = new BinaryNode(node, operator, parseUnary());
                operator = consumeOperator("*/%");
            }
            return node;
        }

        private Node parseUnary() throws QuestException {
            if (position < end && expression.charAt(position) == '-') {
                position++;
                if (position >= end) {
                    throw new QuestException("Invalid calculation (negation missing value)");
                }
                return new NegationNode(parsePower());
            }
            return parsePower();
        }

        private Node parsePower() throws QuestException {
            final Node base = parsePrimary();
            if (consumeOperator("^") != 0) {
                return new BinaryNode(base, '^', parseUnary());
            }
            return base;
        }

        private Node parsePrimary() throws QuestException {
            if (position >= end) {
                if (afterOperator) {
                    throw new QuestException("Invalid calculation (operator missing second value)");
                }
                throw new QuestException("Missing calculation");
            }
            final char chr = expression.charAt(position);
            if (chr == '{') {
                return parseCurlyBracePlaceholder();
            }
            if (chr == '(' || chr == '[') {
                return parseGroup(chr);
            }
            if (chr == '|') {
                return parseAbsolute();
            }
            final Matcher numberMatcher = FP_REGEX.matcher(expression).region(position, end);
            if (numberMatcher.lookingAt()) {
                position = numberMatcher.end();
                return new NumberNode(numberMatcher.group());
            }
            if (Operator.isOperator(chr)) {
                if (afterOperator) {
                    throw new QuestException("Invalid calculation (doubled operators)");
                }
                throw new QuestException("Invalid calculation (operator missing first value)");
            }
            return parsePlaceholder();
        }

        private Node parseCurlyBracePlaceholder() throws QuestException {
            final int close = findCurlyBraceEnd(position + 1);
            final String name = ESCAPE_REGEX.matcher(expression.substring(position + 1, close)).replaceAll("$1");
            if (name.isEmpty()) {
                throw new QuestException("Invalid calculation (empty placeholder)");
            }
            position = close + 1;
            return parameter(name);
        }

        private Node parseGroup(final char opening) throws QuestException {
            final int close = findParenthesisEnd(position + 1);
            if (close == position + 1) {
                throw new QuestException("Invalid calculation (empty parenthesis)");
            }
            final char closing = expression.charAt(close);
            if (opening == '(' && closing != ')' || opening == '[' && closing != ']') {
                throw new QuestException("Invalid calculation (parenthesis / brackets mismatch)");
            }
            final Node inner = parseRange(position + 1, close);
            position = close + 1;
            return new GroupNode(inner);
        }

        private Node parseAbsolute() throws QuestException {
            final int close = findAbsoluteEnd(position + 1);
            if (close == position + 1) {
                throw new QuestException("Invalid calculation (empty absolute value)");
            }
            final Node inner = parseRange(position + 1, close);
            position = close + 1;
            return new AbsNode(inner);
        }

        private Node parsePlaceholder() throws QuestException {
            final int start = position;
            while (position < end) {
                final char chr = expression.charAt(position);
                if (Operator.isOperator(chr) || PLACEHOLDER_END.indexOf(chr) >= 0) {
                    break;
                }
                position++;
            }
            if (position == start) {
                throw unexpected(expression.charAt(position));
            }
            return parameter(expression.substring(start, position));
        }

        private Node parameter(final String placeholder) {
            return new ParameterNode(parameters.computeIfAbsent(placeholder, key -> parameterName(parameters.size())));
        }

        private char consumeOperator(final String operators) {
            if (position < end) {
                final char chr = expression.charAt(position);
                if (operators.indexOf(chr) >= 0) {
                    position++;
                    afterOperator = true;
                    return chr;
                }
            }
            return 0;
        }

        private QuestException unexpected(final char chr) {
            if (chr == ')' || chr == ']') {
                return new QuestException("Invalid calculation (unbalanced parenthesis)");
            }
            if (chr == '}') {
                return new QuestException("Invalid calculation (unbalanced curly brace)");
            }
            return new QuestException("Invalid calculation (missing operator)");
        }

        private int findAbsoluteEnd(final int startIndex) throws QuestException {
            int index = startIndex;
            while (index < end) {
                switch (expression.charAt(index)) {
                    case '{' -> index = findCurlyBraceEnd(index + 1);
                    case '(', '[' -> index = findParenthesisEnd(index + 1);
                    case '|' -> {
                        return index;
                    }
                    default -> {
                    }
                }
                index++;
            }
            throw new QuestException("Invalid calculation (unbalanced absolute value)");
        }

        private int findParenthesisEnd(final int startIndex) throws QuestException {
            int depth = 1;
            int index = startIndex;
            while (index < end) {
                switch (expression.charAt(index)) {
                    case '{' -> index = findCurlyBraceEnd(index + 1);
                    case '(', '[' -> depth++;
                    case ')', ']' -> {
                        depth--;
                        if (depth == 0) {
                            return index;
                        }
                    }
                    default -> {
                    }
                }
                index++;
            }
            throw new QuestException("Invalid calculation (unbalanced parenthesis)");
        }

        private int findCurlyBraceEnd(final int startIndex) throws QuestException {
            int index = startIndex;
            while (index < end) {
                final char chr = expression.charAt(index);
                if (chr == '\\') {
                    index++;
                } else if (chr == '}') {
                    return index;
                }
                index++;
            }
            throw new QuestException("Invalid calculation (unbalanced curly brace)");
        }

        private String parameterName(final int index) {
            final StringBuilder name = new StringBuilder();
            int remaining = index;
            do {
                name.insert(0, (char) ('a' + remaining % ALPHABET_SIZE));
                remaining = remaining / ALPHABET_SIZE - 1;
            } while (remaining >= 0);
            return name.toString();
        }
    }
}
