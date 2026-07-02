package org.betonquest.betonquest.lib.function;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.lib.function.token.DefaultTokens;
import org.betonquest.betonquest.lib.function.token.FunctionToken;
import org.betonquest.betonquest.lib.function.token.FunctionTokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to scan through a list of {@link FunctionToken}s.
 *
 * @since 3.1.0
 */
public class TokenScanner {

    /**
     * The list of tokens to scan.
     */
    private final List<FunctionToken> tokens;

    /**
     * The current position in the list of tokens.
     */
    private int pointer;

    /**
     * Creates a new scanner for the given list of tokens.
     * Automatically removes all {@link FunctionTokenType#SPACE} tokens since they are ignored for parsing.
     *
     * @param tokens the list of tokens to scan
     * @since 3.1.0
     */
    public TokenScanner(final List<FunctionToken> tokens) {
        this.tokens = new ArrayList<>(tokens);
        this.tokens.removeIf(token -> token.type() == FunctionTokenType.SPACE);
    }

    /**
     * Returns the current token without advancing the scanner.
     *
     * @return the current token
     * @since 3.1.0
     */
    public FunctionToken peek() {
        return peek(0);
    }

    /**
     * Returns the token at the given position ahead of the current position.
     *
     * @param ahead the number of tokens ahead of the current position
     * @return the token at the given position
     * @since 3.1.0
     */
    public FunctionToken peek(final int ahead) {
        return tokens.size() > pointer + ahead ? tokens.get(pointer + ahead) : DefaultTokens.INVALID;
    }

    /**
     * Returns true if the current token is of the given type.
     *
     * @param type the type to check
     * @return true if the current token is of the given type, false otherwise
     * @since 3.1.0
     */
    public boolean peek(final FunctionTokenType type) {
        return peek(type, 0);
    }

    /**
     * Returns true if the token at the given position ahead of the current position is of the given type.
     *
     * @param type  the type to check
     * @param ahead the number of tokens ahead of the current position
     * @return true if the token at the given position is of the given type, false otherwise
     * @since 3.1.0
     */
    public boolean peek(final FunctionTokenType type, final int ahead) {
        return peek(ahead).type() == type;
    }

    /**
     * Returns true if the current token is equal to the given token.
     *
     * @param token the token to check
     * @return true if the current token is equal to the given token, false otherwise
     * @since 3.1.0
     */
    public boolean peek(final FunctionToken token) {
        return peek(token, 0);
    }

    /**
     * Returns true if the token at the given position ahead of the current position is equal to the given token.
     *
     * @param token the token to check
     * @param ahead the number of tokens ahead of the current position
     * @return true if the token at the given position is equal to the given token, false otherwise
     * @since 3.1.0
     */
    public boolean peek(final FunctionToken token, final int ahead) {
        return peek(ahead).equals(token);
    }

    /**
     * Returns the current token and advances the scanner by one position afterward.
     *
     * @return the current token
     * @throws QuestException if the scanner is at the end of the list of tokens
     * @since 3.1.0
     */
    public FunctionToken consume() throws QuestException {
        if (isAtEnd()) {
            throw new QuestException("There are no more tokens to consume.");
        }
        return tokens.get(pointer++);
    }

    /**
     * Advances the scanner by one position if it matches the given type. Throws an exception otherwise.
     *
     * @param errorMessage the error message to display if the token does not match the expected type
     * @param expected     the expected type of the token
     * @throws QuestException if the token does not match the expected type
     * @since 3.1.0
     */
    public void consume(final FunctionTokenType expected, final String errorMessage) throws QuestException {
        if (isAtEnd()) {
            throw new QuestException("There are no more tokens to consume: %s".formatted(errorMessage));
        }
        if (peek().type() != expected) {
            throw new QuestException("Error while consuming function token. Unexpected type: '%s' != '%s'. %s".formatted(peek().type(), expected, errorMessage));
        }
        pointer++;
    }

    /**
     * Advances the scanner by one position if it matches the given token. Throws an exception otherwise.
     *
     * @param errorMessage the error message to display if the token does not match the expected token
     * @param expected     the expected token
     * @throws QuestException if the token does not match the expected token
     * @since 3.1.0
     */
    public void consume(final FunctionToken expected, final String errorMessage) throws QuestException {
        if (isAtEnd()) {
            throw new QuestException("There are no more tokens to consume: %s".formatted(errorMessage));
        }
        if (!peek().equals(expected)) {
            throw new QuestException("Error while consuming function token. Unexpected type: '%s' != '%s'. %s".formatted(peek().type(), expected, errorMessage));
        }
        pointer++;
    }

    /**
     * Checks if the scanner is at the end of the list of tokens.
     *
     * @return true if the scanner is at the end, false otherwise
     * @since 3.1.0
     */
    public boolean isAtEnd() {
        return pointer >= tokens.size();
    }
}
