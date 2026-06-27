package org.betonquest.betonquest.lib.function.symbols;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.lib.function.TokenScanner;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A builder for {@link NonTerminalSymbol}s.
 *
 * @param <T> the type of the symbol's result value
 * @since 3.1.0
 */
public class NonTerminalSymbolBuilder<T> {

    /**
     * The branches in order of precedence before falling back to the default parser.
     */
    private final List<Map.Entry<Predicate<TokenScanner>, QuestFunction<TokenScanner, T>>> branches;

    /**
     * The prefixes to match before parsing the entire symbol.
     */
    private final List<Predicate<TokenScanner>> prefixes;

    /**
     * The default parser to use if no branch matches.
     * Throws an exception if no default parser is specified when attempting to build the symbol.
     */
    @Nullable
    private QuestFunction<TokenScanner, T> parseDefault;

    /**
     * Creates a new NonTerminalSymbolBuilder.
     *
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder() {
        this.branches = new ArrayList<>();
        this.prefixes = new ArrayList<>();
        this.parseDefault = null;
    }

    /**
     * Adds a prefix requirement to the symbol.
     * Every following prefix will be matched in order.
     *
     * @param prefix the prefix to match
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> prefix(final FunctionTokenType prefix) {
        final int position = prefixes.size();
        prefixes.add(scanner -> scanner.peek(prefix, position));
        return this;
    }

    /**
     * Adds a prefix requirement to the symbol.
     * Every following prefix will be matched in order.
     *
     * @param token the token to match
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> prefix(final FunctionToken token) {
        final int position = prefixes.size();
        prefixes.add(scanner -> scanner.peek(token, position));
        return this;
    }

    /**
     * Adds a prefix requirement to the symbol.
     *
     * @param predicate the predicate to match; the first argument is the scanner, and the second is the position of the prefix
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> prefix(final BiPredicate<TokenScanner, Integer> predicate) {
        final int position = prefixes.size();
        prefixes.add(scanner -> predicate.test(scanner, position));
        return this;
    }

    /**
     * Adds a branch to the symbol.
     * Branches are matched in order as alternate parsing methods before falling back to the default parser.
     *
     * @param predicate the predicate to match for this branch to be used
     * @param parse     the parser to use if the predicate matches
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> branch(final Predicate<TokenScanner> predicate, final QuestFunction<TokenScanner, T> parse) {
        branches.add(Map.entry(predicate, parse));
        return this;
    }

    /**
     * Adds a branch to the symbol.
     * Branches are matched in order as alternate parsing methods before falling back to the default parser.
     *
     * @param symbol the {@link NonTerminalSymbol} to branch to
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> branch(final NonTerminalSymbol<T> symbol) {
        return branch(symbol::matches, symbol::parse);
    }

    /**
     * Adds a branch to the symbol.
     * Branches are matched in order as alternate parsing methods before falling back to the default parser.
     *
     * @param prefix the prefix token type to match for this branch to be used
     * @param parse  the parser to use if the token type matches
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> branch(final FunctionTokenType prefix, final QuestFunction<TokenScanner, T> parse) {
        return branch(scanner -> scanner.peek(prefix), parse);
    }

    /**
     * Defines the default parser fallback for the symbol.
     *
     * @param parseDefault the default parser
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> parse(final QuestFunction<TokenScanner, T> parseDefault) {
        this.parseDefault = parseDefault;
        return this;
    }

    /**
     * Defines the default fallback symbol for this symbol.
     *
     * @param parseDefault the default parser
     * @return this builder
     * @since 3.1.0
     */
    public NonTerminalSymbolBuilder<T> parse(final NonTerminalSymbol<T> parseDefault) {
        return parse(parseDefault::parse);
    }

    /**
     * Builds the NonTerminalSymbol from the current state of the builder.
     *
     * @return the built {@link NonTerminalSymbol}
     * @since 3.1.0
     */
    public NonTerminalSymbol<T> build() {
        if (parseDefault == null) {
            throw new IllegalStateException("Invalid non terminal symbol definition. No parse function specified.");
        }
        final QuestFunction<TokenScanner, T> parserFunction = parseDefault;
        return new NonTerminalSymbol<>() {
            @Override
            public boolean matches(final TokenScanner scanner) {
                return prefixes.stream().allMatch(predicate -> predicate.test(scanner));
            }

            @Override
            public T parse(final TokenScanner scanner) throws QuestException {
                for (final Map.Entry<Predicate<TokenScanner>, QuestFunction<TokenScanner, T>> branch : branches) {
                    if (branch.getKey().test(scanner)) {
                        return branch.getValue().apply(scanner);
                    }
                }
                return parserFunction.apply(scanner);
            }
        };
    }
}
