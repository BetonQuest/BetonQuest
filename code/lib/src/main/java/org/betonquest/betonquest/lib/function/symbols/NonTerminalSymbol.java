package org.betonquest.betonquest.lib.function.symbols;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.lib.function.TokenScanner;

/**
 * Represents a single atom in a function expression parser.
 *
 * @param <T> the type to parse the {@link NonTerminalSymbol} into
 * @since 3.1.0
 */
public interface NonTerminalSymbol<T> {

    /**
     * Checks if the current token matches the atom.
     *
     * @param scanner the scanner to check
     * @return true if the token matches, false otherwise
     * @since 3.1.0
     */
    boolean matches(TokenScanner scanner);

    /**
     * Parses the atom into a function expression by consuming the tokens in the scanner.
     *
     * @param scanner the scanner to parse from
     * @return the parsed function expression
     * @throws QuestException if the atom cannot be parsed
     * @since 3.1.0
     */
    T parse(TokenScanner scanner) throws QuestException;
}
